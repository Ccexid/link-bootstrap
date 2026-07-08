package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostCollectPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostLikePO;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityPostInteractionResponseVO;

public interface CommunityPostInteractionService extends IService<CommunityPostPO> {

    /**
     * 点赞社区内容。
     */
    CommunityPostInteractionResponseVO like(Long postId);
    /**
     * 取消社区内容点赞。
     */
    CommunityPostInteractionResponseVO unlike(Long postId);
    /**
     * 收藏社区内容。
     */
    CommunityPostInteractionResponseVO collect(Long postId);
    /**
     * 取消社区内容收藏。
     */
    CommunityPostInteractionResponseVO uncollect(Long postId);
    /**
     * 查询社区内容互动状态。
     */
    CommunityPostInteractionResponseVO interaction(Long postId);
}
