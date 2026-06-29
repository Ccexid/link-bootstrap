package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityCommentInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityPostInternalService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityCommentPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

/**
 * 社区评论服务，负责一级评论、回复、本人编辑删除和计数字段维护。
 */
@Service
@RequiredArgsConstructor
public class CommunityCommentApplicationService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("post_id", "post_id"),
            Map.entry("parent_id", "parent_id"),
            Map.entry("root_id", "root_id"),
            Map.entry("author_id", "author_id"),
            Map.entry("like_count", "like_count"),
            Map.entry("reply_count", "reply_count"),
            Map.entry("created_at", "create_time"),
            Map.entry("updated_at", "update_time")
    );

    private final CommunityCommentInternalService communityCommentInternalService;
    private final CommunityPostInternalService communityPostInternalService;

    @Transactional
    public CommunityCommentPO create(CommunityCommentCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long userId = SecurityHelper.getRequiredUserId();
        CommunityPostPO post = requirePost(request.getPostId(), tenantId);
        CommunityCommentPO parent = requireParent(request.getParentId(), post, tenantId);

        CommunityCommentPO comment = new CommunityCommentPO();
        comment.setTenantId(tenantId);
        comment.setPostId(post.getId());
        comment.setParentId(parent == null ? 0L : parent.getId());
        comment.setRootId(parent == null ? 0L : resolveRootId(parent));
        comment.setAuthorId(userId);
        comment.setReplyToId(parent == null ? null : parent.getAuthorId());
        comment.setContent(normalizeContent(request.getContent()));
        comment.setLikeCount(0L);
        comment.setReplyCount(0L);
        comment.setStatus(StatusEnum.NORMAL);
        communityCommentInternalService.save(comment);
        incrementPostCommentCount(post.getId());
        if (parent != null) {
            incrementParentReplyCount(parent.getId());
        }
        return comment;
    }

    public CommunityCommentPO get(Long id) {
        CommunityCommentPO comment = ApplicationAssert.requireFound(communityCommentInternalService.getById(id), ErrorCode.COMMUNITY_COMMENT_NOT_FOUND);
        ensureCurrentTenant(comment, SecurityHelper.getRequiredTenantId());
        return comment;
    }

    public PageResult<CommunityCommentPO> page(CommunityCommentPageRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        if (request.getPostId() != null) {
            requirePost(request.getPostId(), tenantId);
        }
        Page<CommunityCommentPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        boolean defaultOrder = request.getSortingFields() == null || request.getSortingFields().isEmpty();
        LambdaQueryWrapper<CommunityCommentPO> wrapper = new LambdaQueryWrapper<CommunityCommentPO>()
                .eq(CommunityCommentPO::getTenantId, tenantId)
                .eq(request.getPostId() != null, CommunityCommentPO::getPostId, request.getPostId())
                .eq(request.getRootId() != null, CommunityCommentPO::getRootId, request.getRootId())
                .eq(request.getAuthorId() != null, CommunityCommentPO::getAuthorId, request.getAuthorId())
                .eq(request.getStatus() != null, CommunityCommentPO::getStatus, request.getStatus())
                .orderByAsc(defaultOrder, CommunityCommentPO::getId);
        Page<CommunityCommentPO> result = communityCommentInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Transactional
    public CommunityCommentPO update(Long id, CommunityCommentUpdateRequest request) {
        CommunityCommentPO comment = get(id);
        ensureAuthor(comment, SecurityHelper.getRequiredUserId());
        comment.setContent(normalizeContent(request.getContent()));
        ApplicationAssert.requireSuccess(communityCommentInternalService.updateById(comment), ErrorCode.COMMUNITY_COMMENT_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        CommunityCommentPO comment = get(id);
        ensureAuthor(comment, SecurityHelper.getRequiredUserId());
        ApplicationAssert.requireSuccess(communityCommentInternalService.removeById(id), ErrorCode.COMMUNITY_COMMENT_NOT_FOUND);
        decrementPostCommentCount(comment.getPostId());
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            decrementParentReplyCount(comment.getParentId());
        }
    }

    private CommunityPostPO requirePost(Long postId, Long tenantId) {
        CommunityPostPO post = communityPostInternalService.getById(postId);
        if (post == null || !Objects.equals(post.getTenantId(), tenantId) || post.getStatus() == StatusEnum.DISABLE) {
            throw new BusinessException(ErrorCode.COMMUNITY_POST_NOT_FOUND);
        }
        return post;
    }

    private CommunityCommentPO requireParent(Long parentId, CommunityPostPO post, Long tenantId) {
        if (parentId == null || parentId == 0) {
            return null;
        }
        if (parentId < 0) {
            ApplicationAssert.invalidParam("父评论ID不能小于0");
        }
        CommunityCommentPO parent = communityCommentInternalService.getById(parentId);
        if (parent == null
                || !Objects.equals(parent.getTenantId(), tenantId)
                || !Objects.equals(parent.getPostId(), post.getId())
                || parent.getStatus() == StatusEnum.DISABLE) {
            throw new BusinessException(ErrorCode.COMMUNITY_COMMENT_NOT_FOUND);
        }
        return parent;
    }

    private static Long resolveRootId(CommunityCommentPO parent) {
        if (parent.getRootId() == null || parent.getRootId() == 0) {
            return parent.getId();
        }
        return parent.getRootId();
    }

    private void incrementPostCommentCount(Long postId) {
        communityPostInternalService.update(new UpdateWrapper<CommunityPostPO>()
                .eq("id", postId)
                .setSql("comment_count = comment_count + 1"));
    }

    private void decrementPostCommentCount(Long postId) {
        communityPostInternalService.update(new UpdateWrapper<CommunityPostPO>()
                .eq("id", postId)
                .gt("comment_count", 0)
                .setSql("comment_count = comment_count - 1"));
    }

    private void incrementParentReplyCount(Long parentId) {
        communityCommentInternalService.update(new UpdateWrapper<CommunityCommentPO>()
                .eq("id", parentId)
                .setSql("reply_count = reply_count + 1"));
    }

    private void decrementParentReplyCount(Long parentId) {
        communityCommentInternalService.update(new UpdateWrapper<CommunityCommentPO>()
                .eq("id", parentId)
                .gt("reply_count", 0)
                .setSql("reply_count = reply_count - 1"));
    }

    private static void ensureCurrentTenant(CommunityCommentPO comment, Long tenantId) {
        if (!Objects.equals(comment.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.COMMUNITY_COMMENT_NOT_FOUND);
        }
    }

    private static void ensureAuthor(CommunityCommentPO comment, Long userId) {
        if (!Objects.equals(comment.getAuthorId(), userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能操作本人评论");
        }
    }

    private static String normalizeContent(String content) {
        if (StrUtil.isBlank(content)) {
            ApplicationAssert.invalidParam("评论内容不能为空");
        }
        return content.trim();
    }
}
