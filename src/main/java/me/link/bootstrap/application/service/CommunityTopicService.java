package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityTopicResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface CommunityTopicService extends IService<CommunityTopicPO> {

    CommunityTopicResponseVO create(CommunityTopicCreateRequest request);
    CommunityTopicResponseVO get(Long id);
    PageResult<CommunityTopicResponseVO> page(CommunityTopicPageRequest request);
    CommunityTopicResponseVO update(Long id, CommunityTopicUpdateRequest request);
    void delete(Long id);
}
