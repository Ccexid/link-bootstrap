package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityPostResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface CommunityPostService extends IService<CommunityPostPO> {

    /**
     * 创建社区帖子。
     */
    CommunityPostResponseVO create(CommunityPostCreateRequest request);
    /**
     * 查询社区帖子详情。
     */
    CommunityPostResponseVO get(Long id);
    /**
     * 分页查询社区帖子列表。
     */
    PageResult<CommunityPostResponseVO> page(CommunityPostPageRequest request);
    /**
     * 更新社区帖子。
     */
    CommunityPostResponseVO update(Long id, CommunityPostUpdateRequest request);
    /**
     * 删除社区帖子。
     */
    void delete(Long id);
}
