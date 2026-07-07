package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.CommunitySectionResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface CommunitySectionService extends IService<CommunitySectionPO> {

    CommunitySectionResponseVO create(CommunitySectionCreateRequest request);
    CommunitySectionResponseVO get(Long id);
    PageResult<CommunitySectionResponseVO> page(CommunitySectionPageRequest request);
    CommunitySectionResponseVO update(Long id, CommunitySectionUpdateRequest request);
    void delete(Long id);
}
