package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.infrastructure.crypto.MobileCryptoService;
import me.link.bootstrap.infrastructure.crypto.ProtectedMobile;
import me.link.bootstrap.infrastructure.persistence.internal.TenantInternalService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import me.link.bootstrap.infrastructure.persistence.repository.support.PageOrderHelper;
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
public class TenantApplicationService {

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

    private final TenantInternalService tenantInternalService;
    private final MobileCryptoService mobileCryptoService;

    @Transactional
    public TenantPO create(TenantCreateRequest request) {
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
        tenantInternalService.save(tenant);
        return tenant;
    }

    public TenantPO get(Long id) {
        return ApplicationAssert.requireFound(tenantInternalService.getById(id), ErrorCode.TENANT_NOT_FOUND);
    }

    public PageResult<TenantPO> page(TenantPageRequest request) {
        Page<TenantPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<TenantPO> wrapper = new LambdaQueryWrapper<TenantPO>()
                .like(StrUtil.isNotBlank(request.getName()), TenantPO::getName, request.getName())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), TenantPO::getId);
        Page<TenantPO> result = tenantInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Transactional
    public TenantPO update(Long id, TenantUpdateRequest request) {
        TenantPO tenant = get(id);
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
        ApplicationAssert.requireSuccess(tenantInternalService.updateById(tenant), ErrorCode.TENANT_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(tenantInternalService.removeById(id), ErrorCode.TENANT_NOT_FOUND);
    }

    private void applyContactMobileProtection(TenantPO tenantPO, String contactMobile) {
        ProtectedMobile protectedMobile = mobileCryptoService.protect(normalizeContactMobile(contactMobile));
        tenantPO.setContactMobileCipher(protectedMobile.cipher());
        tenantPO.setContactMobileHash(protectedMobile.hash());
        tenantPO.setContactMobileMask(protectedMobile.mask());
        tenantPO.setContactMobileKeyVersion(protectedMobile.keyVersion());
    }

    private static String normalizeContactMobile(String contactMobile) {
        if (StrUtil.isBlank(contactMobile)) {
            return null;
        }
        String normalizedContactMobile = contactMobile.trim();
        if (normalizedContactMobile.length() > CONTACT_MOBILE_MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("联系手机长度不能超过%d个字符", CONTACT_MOBILE_MAX_LENGTH));
        }
        if (!CONTACT_MOBILE_PATTERN.matcher(normalizedContactMobile).matches()) {
            throw new IllegalArgumentException("联系手机格式不正确");
        }
        return normalizedContactMobile;
    }

    private static Set<String> normalizeWebsites(Set<String> websites) {
        if (websites == null || websites.isEmpty()) {
            return null;
        }
        if (websites.size() > WEBSITE_MAX_COUNT) {
            throw new IllegalArgumentException(String.format("绑定域名数量不能超过%d个", WEBSITE_MAX_COUNT));
        }
        Set<String> normalizedWebsites = new LinkedHashSet<>();
        for (String website : websites) {
            if (StrUtil.isBlank(website)) {
                throw new IllegalArgumentException("绑定域名不能为空");
            }
            String normalizedWebsite = website.trim().toLowerCase();
            if (normalizedWebsite.length() > WEBSITE_MAX_LENGTH) {
                throw new IllegalArgumentException(String.format("绑定域名长度不能超过%d个字符", WEBSITE_MAX_LENGTH));
            }
            if (!WEBSITE_PATTERN.matcher(normalizedWebsite).matches()) {
                throw new IllegalArgumentException(String.format("绑定域名格式不正确: %s", normalizedWebsite));
            }
            normalizedWebsites.add(normalizedWebsite);
        }
        return normalizedWebsites;
    }
}
