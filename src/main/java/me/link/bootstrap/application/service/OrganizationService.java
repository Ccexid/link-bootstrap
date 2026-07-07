package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationCreateRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationPageRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.OrganizationResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface OrganizationService extends IService<OrganizationPO> {

    OrganizationResponseVO create(OrganizationCreateRequest request);
    OrganizationResponseVO get(Long id);
    PageResult<OrganizationResponseVO> page(OrganizationPageRequest request);
    OrganizationResponseVO update(Long id, OrganizationUpdateRequest request);
    void delete(Long id);
}
