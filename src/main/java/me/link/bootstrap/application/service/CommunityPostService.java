package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityPostResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface CommunityPostService extends IService<CommunityPostPO> {

    CommunityPostResponseVO create(CommunityPostCreateRequest request);
    CommunityPostResponseVO get(Long id);
    PageResult<CommunityPostResponseVO> page(CommunityPostPageRequest request);
    CommunityPostResponseVO update(Long id, CommunityPostUpdateRequest request);
    void delete(Long id);
}
