package me.link.bootstrap.application.service.impl;

import me.link.bootstrap.application.service.TenantPackageService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.mapper.TenantPackageMapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import me.link.bootstrap.interfaces.dto.response.vo.TenantPackageResponseVO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackageCreateRequest;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackagePageRequest;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackageUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 租户套餐服务，按轻量 CRUD 结构直接编排校验、事务和持久化。
 */
@Service
@RequiredArgsConstructor
public class TenantPackageServiceImpl extends ServiceImpl<TenantPackageMapper, TenantPackagePO> implements TenantPackageService {

    private static final int NAME_MAX_LENGTH = 30;
    private static final int REMARK_MAX_LENGTH = 256;
    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "name", "name",
            "created_at", "create_time",
            "updated_at", "update_time"
    );

    /**
     * 创建租户套餐。
     */
    @Transactional
    public TenantPackageResponseVO create(TenantPackageCreateRequest request) {
        TenantPackagePO tenantPackage = new TenantPackagePO();
        tenantPackage.setName(normalizeName(request.getName()));
        tenantPackage.setRemark(normalizeRemark(request.getRemark()));
        tenantPackage.setMenuIds(normalizeMenuIds(request.getMenuIds()));
        tenantPackage.setStatus(StatusEnum.NORMAL);
        save(tenantPackage);
        return toResponse(tenantPackage);
    }

    /**
     * 查询租户套餐详情。
     */
    public TenantPackageResponseVO get(Long id) {
        return toResponse(getRequired(id));
    }

    /**
     * 分页查询租户套餐列表。
     */
    public PageResult<TenantPackageResponseVO> page(TenantPackagePageRequest request) {
        Page<TenantPackagePO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<TenantPackagePO> wrapper = new LambdaQueryWrapper<TenantPackagePO>()
                .like(StrUtil.isNotBlank(request.getName()), TenantPackagePO::getName, request.getName())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), TenantPackagePO::getId);
        Page<TenantPackagePO> result = page(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toResponse).toList(), result.getTotal());
    }

    /**
     * 更新租户套餐。
     */
    @Transactional
    public TenantPackageResponseVO update(Long id, TenantPackageUpdateRequest request) {
        TenantPackagePO tenantPackage = getRequired(id);
        tenantPackage.setName(normalizeName(request.getName()));
        tenantPackage.setRemark(normalizeRemark(request.getRemark()));
        tenantPackage.setMenuIds(normalizeMenuIds(request.getMenuIds()));
        if (Boolean.TRUE.equals(request.getEnabled())) {
            tenantPackage.setStatus(StatusEnum.NORMAL);
        }
        if (Boolean.FALSE.equals(request.getEnabled())) {
            tenantPackage.setStatus(StatusEnum.DISABLE);
        }
        ApplicationAssert.requireSuccess(updateById(tenantPackage), ErrorCode.TENANT_PACKAGE_NOT_FOUND);
        return get(id);
    }

    /**
     * 删除租户套餐。
     */
    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(removeById(id), ErrorCode.TENANT_PACKAGE_NOT_FOUND);
    }

    /**
     * 规范化名称。
     */
    private static String normalizeName(String name) {
        if (StrUtil.isBlank(name)) {
            ApplicationAssert.invalidParam("套餐名不能为空");
        }
        String normalizedName = name.trim();
        if (normalizedName.length() > NAME_MAX_LENGTH) {
            ApplicationAssert.invalidParam(String.format("套餐名长度不能超过%d个字符", NAME_MAX_LENGTH));
        }
        return normalizedName;
    }

    /**
     * 规范化备注。
     */
    private static String normalizeRemark(String remark) {
        if (StrUtil.isBlank(remark)) {
            return "";
        }
        String normalizedRemark = remark.trim();
        if (normalizedRemark.length() > REMARK_MAX_LENGTH) {
            ApplicationAssert.invalidParam(String.format("备注长度不能超过%d个字符", REMARK_MAX_LENGTH));
        }
        return normalizedRemark;
    }

    /**
     * 规范化菜单ID集合。
     */
    private static Set<Long> normalizeMenuIds(Set<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            ApplicationAssert.invalidParam("关联菜单不能为空");
        }
        Set<Long> normalizedMenuIds = new LinkedHashSet<>();
        for (Long menuId : menuIds) {
            if (menuId == null || menuId <= 0) {
                ApplicationAssert.invalidParam("关联菜单编号必须大于0");
            }
            normalizedMenuIds.add(menuId);
        }
        return normalizedMenuIds;
    }

    /**
     * 获取必需的业务对象。
     */
    private TenantPackagePO getRequired(Long id) {
        return ApplicationAssert.requireFound(getById(id), ErrorCode.TENANT_PACKAGE_NOT_FOUND);
    }

    /**
     * 转换为响应对象。
     */
    private TenantPackageResponseVO toResponse(TenantPackagePO source) {
        TenantPackageResponseVO response = BeanUtil.copyProperties(source, TenantPackageResponseVO.class);
        response.setCreatedAt(source.getCreateTime());
        response.setUpdatedAt(source.getUpdateTime());
        return response;
    }
}
