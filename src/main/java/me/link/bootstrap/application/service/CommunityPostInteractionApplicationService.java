package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityPostCollectInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityPostInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityPostLikeInternalService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostCollectPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostLikePO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityPostInteractionResponseVO;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 社区帖子互动服务，负责点赞、收藏和当前用户互动状态查询。
 */
@Service
@RequiredArgsConstructor
public class CommunityPostInteractionApplicationService {

    private final CommunityPostInternalService communityPostInternalService;
    private final CommunityPostLikeInternalService communityPostLikeInternalService;
    private final CommunityPostCollectInternalService communityPostCollectInternalService;

    @Transactional
    public CommunityPostInteractionResponseVO like(Long postId) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long userId = SecurityHelper.getRequiredUserId();
        CommunityPostPO post = requirePost(postId, tenantId);
        if (!hasLiked(postId, tenantId, userId)) {
            CommunityPostLikePO like = new CommunityPostLikePO();
            like.setTenantId(tenantId);
            like.setPostId(postId);
            like.setUserId(userId);
            try {
                communityPostLikeInternalService.save(like);
                incrementPostCounter(postId, "like_count");
            } catch (DuplicateKeyException ignored) {
                // 唯一键兜底并发重复点赞，接口保持幂等。
            }
        }
        return interaction(post.getId());
    }

    @Transactional
    public CommunityPostInteractionResponseVO unlike(Long postId) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long userId = SecurityHelper.getRequiredUserId();
        CommunityPostPO post = requirePost(postId, tenantId);
        boolean removed = communityPostLikeInternalService.remove(new LambdaQueryWrapper<CommunityPostLikePO>()
                .eq(CommunityPostLikePO::getTenantId, tenantId)
                .eq(CommunityPostLikePO::getPostId, postId)
                .eq(CommunityPostLikePO::getUserId, userId));
        if (removed) {
            decrementPostCounter(postId, "like_count");
        }
        return interaction(post.getId());
    }

    @Transactional
    public CommunityPostInteractionResponseVO collect(Long postId) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long userId = SecurityHelper.getRequiredUserId();
        CommunityPostPO post = requirePost(postId, tenantId);
        if (!hasCollected(postId, tenantId, userId)) {
            CommunityPostCollectPO collect = new CommunityPostCollectPO();
            collect.setTenantId(tenantId);
            collect.setPostId(postId);
            collect.setUserId(userId);
            try {
                communityPostCollectInternalService.save(collect);
                incrementPostCounter(postId, "collect_count");
            } catch (DuplicateKeyException ignored) {
                // 唯一键兜底并发重复收藏，接口保持幂等。
            }
        }
        return interaction(post.getId());
    }

    @Transactional
    public CommunityPostInteractionResponseVO uncollect(Long postId) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long userId = SecurityHelper.getRequiredUserId();
        CommunityPostPO post = requirePost(postId, tenantId);
        boolean removed = communityPostCollectInternalService.remove(new LambdaQueryWrapper<CommunityPostCollectPO>()
                .eq(CommunityPostCollectPO::getTenantId, tenantId)
                .eq(CommunityPostCollectPO::getPostId, postId)
                .eq(CommunityPostCollectPO::getUserId, userId));
        if (removed) {
            decrementPostCounter(postId, "collect_count");
        }
        return interaction(post.getId());
    }

    public CommunityPostInteractionResponseVO interaction(Long postId) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        Long userId = SecurityHelper.getRequiredUserId();
        CommunityPostPO post = requirePost(postId, tenantId);
        return new CommunityPostInteractionResponseVO(
                hasLiked(postId, tenantId, userId),
                hasCollected(postId, tenantId, userId),
                post.getLikeCount(),
                post.getCollectCount()
        );
    }

    private CommunityPostPO requirePost(Long postId, Long tenantId) {
        CommunityPostPO post = communityPostInternalService.getById(postId);
        if (post == null || !Objects.equals(post.getTenantId(), tenantId) || post.getStatus() == StatusEnum.DISABLE) {
            throw new BusinessException(ErrorCode.COMMUNITY_POST_NOT_FOUND);
        }
        return post;
    }

    private boolean hasLiked(Long postId, Long tenantId, Long userId) {
        return communityPostLikeInternalService.count(new LambdaQueryWrapper<CommunityPostLikePO>()
                .eq(CommunityPostLikePO::getTenantId, tenantId)
                .eq(CommunityPostLikePO::getPostId, postId)
                .eq(CommunityPostLikePO::getUserId, userId)) > 0;
    }

    private boolean hasCollected(Long postId, Long tenantId, Long userId) {
        return communityPostCollectInternalService.count(new LambdaQueryWrapper<CommunityPostCollectPO>()
                .eq(CommunityPostCollectPO::getTenantId, tenantId)
                .eq(CommunityPostCollectPO::getPostId, postId)
                .eq(CommunityPostCollectPO::getUserId, userId)) > 0;
    }

    private void incrementPostCounter(Long postId, String column) {
        communityPostInternalService.update(new UpdateWrapper<CommunityPostPO>()
                .eq("id", postId)
                .setSql(column + " = " + column + " + 1"));
    }

    private void decrementPostCounter(Long postId, String column) {
        communityPostInternalService.update(new UpdateWrapper<CommunityPostPO>()
                .eq("id", postId)
                .gt(column, 0)
                .setSql(column + " = " + column + " - 1"));
    }
}
