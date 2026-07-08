package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationCreateRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationPageRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.OrganizationResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface OrganizationService extends IService<OrganizationPO> {

    /**
     * 创建组织。
     */
    OrganizationResponseVO create(OrganizationCreateRequest request);
    /**
     * 查询组织详情。
     */
    OrganizationResponseVO get(Long id);
    /**
     * 分页查询组织列表。
     */
    PageResult<OrganizationResponseVO> page(OrganizationPageRequest request);
    /**
     * 更新组织。
     */
    OrganizationResponseVO update(Long id, OrganizationUpdateRequest request);
    /**
     * 删除组织。
     */
    void delete(Long id);
}
