package me.link.bootstrap.application.service.impl;

import me.link.bootstrap.application.service.TenantService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.mapper.TenantMapper;

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
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import me.link.bootstrap.interfaces.dto.response.vo.TenantResponseVO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantCreateRequest;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantPageRequest;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 租户服务，按小体量 CRUD 的轻量结构直接编排校验、事务和持久化。
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends ServiceImpl<TenantMapper, TenantPO> implements TenantService {

    private static final int CONTACT_MOBILE_MAX_LENGTH = 20;
    private static final int WEBSITE_MAX_COUNT = 20;
    private static final int WEBSITE_MAX_LENGTH = 253;
    private static final Pattern CONTACT_MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern WEBSITE_PATTERN = Pattern.compile("^(?=.{1,253}$)(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,63}$");
    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "name", "name",
            "created_at", "create_time",
            "updated_at", "update_time"
    );
    private final MobileCryptoService mobileCryptoService;

    /**
     * 创建租户。
     */
    @Transactional
    public TenantResponseVO create(TenantCreateRequest request) {
        TenantPO tenant = new TenantPO();
        tenant.setName(request.getName().trim());
        tenant.setContactUserId(request.getContactUserId());
        tenant.setContactName(request.getContactName().trim());
        tenant.setStatus(StatusEnum.NORMAL);
        tenant.setWebsites(normalizeWebsites(request.getWebsites()));
        tenant.setPackageId(request.getPackageId());
        tenant.setExpireTime(request.getExpireTime());
        tenant.setAccountCount(request.getAccountCount());
        applyContactMobileProtection(tenant, request.getContactMobile());
        save(tenant);
        return toResponse(tenant);
    }

    /**
     * 查询租户详情。
     */
    public TenantResponseVO get(Long id) {
        return toResponse(getRequired(id));
    }

    /**
     * 分页查询租户列表。
     */
    public PageResult<TenantResponseVO> page(TenantPageRequest request) {
        Page<TenantPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<TenantPO> wrapper = new LambdaQueryWrapper<TenantPO>()
                .like(StrUtil.isNotBlank(request.getName()), TenantPO::getName, request.getName())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), TenantPO::getId);
        Page<TenantPO> result = page(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toResponse).toList(), result.getTotal());
    }

    /**
     * 更新租户。
     */
    @Transactional
    public TenantResponseVO update(Long id, TenantUpdateRequest request) {
        TenantPO tenant = getRequired(id);
        tenant.setContactUserId(request.getContactUserId());
        tenant.setContactName(request.getContactName().trim());
        tenant.setWebsites(normalizeWebsites(request.getWebsites()));
        tenant.setPackageId(request.getPackageId());
        tenant.setExpireTime(request.getExpireTime());
        tenant.setAccountCount(request.getAccountCount());
        if (Boolean.TRUE.equals(request.getEnabled())) {
            tenant.setStatus(StatusEnum.NORMAL);
        }
        if (Boolean.FALSE.equals(request.getEnabled())) {
            tenant.setStatus(StatusEnum.DISABLE);
        }
        applyContactMobileProtection(tenant, request.getContactMobile());
        ApplicationAssert.requireSuccess(updateById(tenant), ErrorCode.TENANT_NOT_FOUND);
        return get(id);
    }

    /**
     * 删除租户。
     */
    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(removeById(id), ErrorCode.TENANT_NOT_FOUND);
    }

    /**
     * 应用联系手机号Protection。
     */
    private void applyContactMobileProtection(TenantPO tenantPO, String contactMobile) {
        ProtectedMobile protectedMobile = mobileCryptoService.protect(normalizeContactMobile(contactMobile));
        tenantPO.setContactMobileCipher(protectedMobile.cipher());
        tenantPO.setContactMobileHash(protectedMobile.hash());
        tenantPO.setContactMobileMask(protectedMobile.mask());
        tenantPO.setContactMobileKeyVersion(protectedMobile.keyVersion());
    }

    /**
     * 规范化联系手机号。
     */
    private static String normalizeContactMobile(String contactMobile) {
        if (StrUtil.isBlank(contactMobile)) {
            return null;
        }
        String normalizedContactMobile = contactMobile.trim();
        if (normalizedContactMobile.length() > CONTACT_MOBILE_MAX_LENGTH) {
            ApplicationAssert.invalidParam(String.format("联系手机长度不能超过%d个字符", CONTACT_MOBILE_MAX_LENGTH));
        }
        if (!CONTACT_MOBILE_PATTERN.matcher(normalizedContactMobile).matches()) {
            ApplicationAssert.invalidParam("联系手机格式不正确");
        }
        return normalizedContactMobile;
    }

    /**
     * 规范化网址。
     */
    private static Set<String> normalizeWebsites(Set<String> websites) {
        if (websites == null || websites.isEmpty()) {
            return null;
        }
        if (websites.size() > WEBSITE_MAX_COUNT) {
            ApplicationAssert.invalidParam(String.format("绑定域名数量不能超过%d个", WEBSITE_MAX_COUNT));
        }
        Set<String> normalizedWebsites = new LinkedHashSet<>();
        for (String website : websites) {
            if (StrUtil.isBlank(website)) {
                ApplicationAssert.invalidParam("绑定域名不能为空");
            }
            String normalizedWebsite = website.trim().toLowerCase();
            if (normalizedWebsite.length() > WEBSITE_MAX_LENGTH) {
                ApplicationAssert.invalidParam(String.format("绑定域名长度不能超过%d个字符", WEBSITE_MAX_LENGTH));
            }
            if (!WEBSITE_PATTERN.matcher(normalizedWebsite).matches()) {
                ApplicationAssert.invalidParam(String.format("绑定域名格式不正确: %s", normalizedWebsite));
            }
            normalizedWebsites.add(normalizedWebsite);
        }
        return normalizedWebsites;
    }

    /**
     * 获取必需的业务对象。
     */
    private TenantPO getRequired(Long id) {
        return ApplicationAssert.requireFound(getById(id), ErrorCode.TENANT_NOT_FOUND);
    }

    /**
     * 转换为响应对象。
     */
    private TenantResponseVO toResponse(TenantPO source) {
        TenantResponseVO response = BeanUtil.copyProperties(source, TenantResponseVO.class);
        response.setContactMobile(source.getContactMobileMask());
        response.setCreatedAt(source.getCreateTime());
        response.setUpdatedAt(source.getUpdateTime());
        return response;
    }
}
