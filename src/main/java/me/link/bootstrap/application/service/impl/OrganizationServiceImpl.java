package me.link.bootstrap.application.service.impl;

import me.link.bootstrap.application.service.OrganizationService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.mapper.OrganizationMapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import me.link.bootstrap.infrastructure.crypto.MobileCryptoService;
import me.link.bootstrap.infrastructure.crypto.ProtectedMobile;
import me.link.bootstrap.application.service.OrganizationService;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import me.link.bootstrap.interfaces.dto.response.vo.OrganizationResponseVO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationCreateRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationPageRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 组织服务，直接编排组织校验、手机号保护、分页查询和持久化。
 * <p>
 * 多租户隔离由 {@code TenantLineInnerInterceptor} 全局处理：所有针对组织表
 * 的 SELECT/UPDATE/DELETE 自动追加 {@code tenant_id = ?} 条件，规避水平越权（IDOR）。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, OrganizationPO> implements OrganizationService {

    private static final Pattern CONTACT_MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "parent_id", "parent_id",
            "tenant_id", "tenant_id"
    );
    private final MobileCryptoService mobileCryptoService;

    @Transactional
    public OrganizationResponseVO create(OrganizationCreateRequest request) {
        OrganizationPO organization = new OrganizationPO();
        applyMutableFields(organization, request.getName(), request.getOrgType(), request.getParentId(), request.getAncestors(), request.getLevel(), request.getContactName(), request.getContactMobile(), request.getStatus());
        organization.setTenantId(SecurityHelper.getRequiredTenantId());
        save(organization);
        return toResponse(organization);
    }

    public OrganizationResponseVO get(Long id) {
        return toResponse(getRequired(id));
    }

    public PageResult<OrganizationResponseVO> page(OrganizationPageRequest request) {
        Page<OrganizationPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<OrganizationPO> wrapper = new LambdaQueryWrapper<OrganizationPO>()
                .like(StrUtil.isNotBlank(request.getName()), OrganizationPO::getName, request.getName())
                .eq(request.getOrgType() != null, OrganizationPO::getOrgType, request.getOrgType())
                .eq(request.getParentId() != null, OrganizationPO::getParentId, request.getParentId())
                .eq(request.getStatus() != null, OrganizationPO::getStatus, request.getStatus())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), OrganizationPO::getId);
        Page<OrganizationPO> result = page(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toResponse).toList(), result.getTotal());
    }

    @Transactional
    public OrganizationResponseVO update(Long id, OrganizationUpdateRequest request) {
        OrganizationPO organization = getRequired(id);
        applyMutableFields(organization, request.getName(), request.getOrgType(), request.getParentId(), request.getAncestors(), request.getLevel(), request.getContactName(), request.getContactMobile(), request.getStatus());
        organization.setTenantId(SecurityHelper.getRequiredTenantId());
        ApplicationAssert.requireSuccess(updateById(organization), ErrorCode.ORGANIZATION_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(removeById(id), ErrorCode.ORGANIZATION_NOT_FOUND);
    }

    private void applyMutableFields(OrganizationPO organization,
                                    String name,
                                    Integer orgType,
                                    Long parentId,
                                    String ancestors,
                                    Integer level,
                                    String contactName,
                                    String contactMobile,
                                    StatusEnum status) {
        if (StrUtil.isBlank(name)) {
            ApplicationAssert.invalidParam("组织name不能为空");
        }
        String normalizedContactMobile = normalizeContactMobile(contactMobile);
        organization.setName(name.trim());
        organization.setOrgType(orgType);
        organization.setParentId(parentId);
        organization.setAncestors(ancestors);
        organization.setLevel(level);
        organization.setContactName(contactName);
        organization.setStatus(status);
        applyContactMobileProtection(organization, normalizedContactMobile);
    }

    private static String normalizeContactMobile(String contactMobile) {
        if (StrUtil.isBlank(contactMobile)) {
            return null;
        }
        String normalizedContactMobile = contactMobile.trim();
        if (!CONTACT_MOBILE_PATTERN.matcher(normalizedContactMobile).matches()) {
            ApplicationAssert.invalidParam("组织联系电话格式不正确");
        }
        return normalizedContactMobile;
    }

    private void applyContactMobileProtection(OrganizationPO organization, String contactMobile) {
        ProtectedMobile protectedMobile = mobileCryptoService.protect(contactMobile);
        organization.setContactMobileCipher(protectedMobile.cipher());
        organization.setContactMobileHash(protectedMobile.hash());
        organization.setContactMobileMask(protectedMobile.mask());
        organization.setContactMobileKeyVersion(protectedMobile.keyVersion());
    }

    private OrganizationPO getRequired(Long id) {
        return ApplicationAssert.requireFound(getById(id), ErrorCode.ORGANIZATION_NOT_FOUND);
    }

    private OrganizationResponseVO toResponse(OrganizationPO source) {
        OrganizationResponseVO response = BeanUtil.copyProperties(source, OrganizationResponseVO.class);
        response.setContactMobile(source.getContactMobileMask());
        response.setCreatedAt(source.getCreateTime());
        response.setUpdatedAt(source.getUpdateTime());
        return response;
    }
}
