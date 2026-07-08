package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.CommunitySectionResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface CommunitySectionService extends IService<CommunitySectionPO> {

    /**
     * 创建社区板块。
     */
    CommunitySectionResponseVO create(CommunitySectionCreateRequest request);
    /**
     * 查询社区板块详情。
     */
    CommunitySectionResponseVO get(Long id);
    /**
     * 分页查询社区板块列表。
     */
    PageResult<CommunitySectionResponseVO> page(CommunitySectionPageRequest request);
    /**
     * 更新社区板块。
     */
    CommunitySectionResponseVO update(Long id, CommunitySectionUpdateRequest request);
    /**
     * 删除社区板块。
     */
    void delete(Long id);
}
