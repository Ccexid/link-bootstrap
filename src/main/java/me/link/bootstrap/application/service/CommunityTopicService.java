package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityTopicResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface CommunityTopicService extends IService<CommunityTopicPO> {

    /**
     * 创建社区话题。
     */
    CommunityTopicResponseVO create(CommunityTopicCreateRequest request);
    /**
     * 查询社区话题详情。
     */
    CommunityTopicResponseVO get(Long id);
    /**
     * 分页查询社区话题列表。
     */
    PageResult<CommunityTopicResponseVO> page(CommunityTopicPageRequest request);
    /**
     * 更新社区话题。
     */
    CommunityTopicResponseVO update(Long id, CommunityTopicUpdateRequest request);
    /**
     * 删除社区话题。
     */
    void delete(Long id);
}
