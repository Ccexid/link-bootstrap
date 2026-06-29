package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityPostInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.CommunitySectionInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityTopicInternalService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostUpdateRequest;
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
 * 社区帖子服务，负责发帖、本人编辑删除、列表和详情查询。
 */
@Service
@RequiredArgsConstructor
public class CommunityPostApplicationService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("section_id", "section_id"),
            Map.entry("topic_id", "topic_id"),
            Map.entry("author_id", "author_id"),
            Map.entry("view_count", "view_count"),
            Map.entry("like_count", "like_count"),
            Map.entry("comment_count", "comment_count"),
            Map.entry("collect_count", "collect_count"),
            Map.entry("pinned", "pinned"),
            Map.entry("featured", "featured"),
            Map.entry("created_at", "create_time"),
            Map.entry("updated_at", "update_time")
    );

    private final CommunityPostInternalService communityPostInternalService;
    private final CommunitySectionInternalService communitySectionInternalService;
    private final CommunityTopicInternalService communityTopicInternalService;

    @Transactional
    public CommunityPostPO create(CommunityPostCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long userId = SecurityHelper.getRequiredUserId();
        validateSection(request.getSectionId(), tenantId);
        validateTopic(request.getTopicId(), request.getSectionId(), tenantId);
        CommunityPostPO post = new CommunityPostPO();
        applyMutableFields(post, request.getSectionId(), request.getTopicId(), request.getTitle(), request.getSummary(),
                request.getContent(), request.getCoverUrl());
        post.setTenantId(tenantId);
        post.setAuthorId(userId);
        post.setViewCount(0L);
        post.setLikeCount(0L);
        post.setCommentCount(0L);
        post.setCollectCount(0L);
        post.setPinned(false);
        post.setFeatured(false);
        post.setStatus(StatusEnum.NORMAL);
        communityPostInternalService.save(post);
        return post;
    }

    public CommunityPostPO get(Long id) {
        CommunityPostPO post = ApplicationAssert.requireFound(communityPostInternalService.getById(id), ErrorCode.COMMUNITY_POST_NOT_FOUND);
        ensureCurrentTenant(post, SecurityHelper.getRequiredTenantId());
        return post;
    }

    public PageResult<CommunityPostPO> page(CommunityPostPageRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        if (request.getSectionId() != null) {
            validateSection(request.getSectionId(), tenantId);
        }
        if (request.getTopicId() != null) {
            validateTopic(request.getTopicId(), request.getSectionId(), tenantId);
        }
        Page<CommunityPostPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        boolean defaultOrder = request.getSortingFields() == null || request.getSortingFields().isEmpty();
        LambdaQueryWrapper<CommunityPostPO> wrapper = new LambdaQueryWrapper<CommunityPostPO>()
                .eq(request.getSectionId() != null, CommunityPostPO::getSectionId, request.getSectionId())
                .eq(request.getTopicId() != null, CommunityPostPO::getTopicId, request.getTopicId())
                .eq(request.getAuthorId() != null, CommunityPostPO::getAuthorId, request.getAuthorId())
                .like(StrUtil.isNotBlank(request.getTitle()), CommunityPostPO::getTitle, request.getTitle())
                .eq(request.getStatus() != null, CommunityPostPO::getStatus, request.getStatus())
                .orderByDesc(defaultOrder, CommunityPostPO::getPinned)
                .orderByDesc(defaultOrder, CommunityPostPO::getFeatured)
                .orderByDesc(defaultOrder, CommunityPostPO::getId);
        Page<CommunityPostPO> result = communityPostInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Transactional
    public CommunityPostPO update(Long id, CommunityPostUpdateRequest request) {
        CommunityPostPO post = get(id);
        ensureAuthor(post, SecurityHelper.getRequiredUserId());
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateSection(request.getSectionId(), tenantId);
        validateTopic(request.getTopicId(), request.getSectionId(), tenantId);
        applyMutableFields(post, request.getSectionId(), request.getTopicId(), request.getTitle(), request.getSummary(),
                request.getContent(), request.getCoverUrl());
        ApplicationAssert.requireSuccess(communityPostInternalService.updateById(post), ErrorCode.COMMUNITY_POST_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        CommunityPostPO post = get(id);
        ensureAuthor(post, SecurityHelper.getRequiredUserId());
        ApplicationAssert.requireSuccess(communityPostInternalService.removeById(id), ErrorCode.COMMUNITY_POST_NOT_FOUND);
    }

    private void applyMutableFields(CommunityPostPO post,
                                    Long sectionId,
                                    Long topicId,
                                    String title,
                                    String summary,
                                    String content,
                                    String coverUrl) {
        if (StrUtil.isBlank(title)) {
            ApplicationAssert.invalidParam("帖子title不能为空");
        }
        if (StrUtil.isBlank(content)) {
            ApplicationAssert.invalidParam("帖子content不能为空");
        }
        post.setSectionId(sectionId);
        post.setTopicId(topicId);
        post.setTitle(title.trim());
        post.setSummary(trimToNull(summary));
        post.setContent(content.trim());
        post.setCoverUrl(trimToNull(coverUrl));
    }

    private void validateSection(Long sectionId, Long tenantId) {
        if (sectionId == null || sectionId <= 0) {
            ApplicationAssert.invalidParam("帖子sectionId必须大于0");
        }
        CommunitySectionPO section = communitySectionInternalService.getById(sectionId);
        if (section == null || !Objects.equals(section.getTenantId(), tenantId) || section.getStatus() == StatusEnum.DISABLE) {
            throw new BusinessException(ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        }
    }

    private void validateTopic(Long topicId, Long sectionId, Long tenantId) {
        if (topicId == null) {
            return;
        }
        CommunityTopicPO topic = communityTopicInternalService.getById(topicId);
        if (topic == null
                || !Objects.equals(topic.getTenantId(), tenantId)
                || (sectionId != null && !Objects.equals(topic.getSectionId(), sectionId))
                || topic.getStatus() == StatusEnum.DISABLE) {
            throw new BusinessException(ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
        }
    }

    private static void ensureCurrentTenant(CommunityPostPO post, Long tenantId) {
        if (!Objects.equals(post.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.COMMUNITY_POST_NOT_FOUND);
        }
    }

    private static void ensureAuthor(CommunityPostPO post, Long userId) {
        if (!Objects.equals(post.getAuthorId(), userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能操作本人帖子");
        }
    }

    private static String trimToNull(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return value.trim();
    }
}
