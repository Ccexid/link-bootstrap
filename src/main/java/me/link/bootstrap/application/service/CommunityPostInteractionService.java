package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostCollectPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostLikePO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityPostInteractionResponseVO;

public interface CommunityPostInteractionService extends IService<CommunityPostPO> {

    CommunityPostInteractionResponseVO like(Long postId);
    CommunityPostInteractionResponseVO unlike(Long postId);
    CommunityPostInteractionResponseVO collect(Long postId);
    CommunityPostInteractionResponseVO uncollect(Long postId);
    CommunityPostInteractionResponseVO interaction(Long postId);
}
