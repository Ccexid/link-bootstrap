package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.infrastructure.persistence.internal.CommunitySectionInternalService;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 社区板块服务，直接编排板块校验、分页查询和持久化。
 */
@Service
@RequiredArgsConstructor
public class CommunitySectionApplicationService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "code", "code",
            "sort", "sort",
            "parent_id", "parent_id"
    );

    private final CommunitySectionInternalService communitySectionInternalService;

    @Transactional
    public CommunitySectionPO create(CommunitySectionCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        String code = normalizeCode(request.getCode());
        ensureCodeUnique(code, null);
        validateParent(request.getParentId(), null, tenantId);
        CommunitySectionPO section = new CommunitySectionPO();
        applyMutableFields(section, request.getName(), code, request.getDescription(), request.getCoverUrl(), request.getParentId(), request.getSort(), request.getStatus());
        section.setTenantId(tenantId);
        communitySectionInternalService.save(section);
        return section;
    }

    public CommunitySectionPO get(Long id) {
        CommunitySectionPO section = ApplicationAssert.requireFound(communitySectionInternalService.getById(id), ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        ensureCurrentTenant(section, SecurityHelper.getRequiredTenantId());
        return section;
    }

    public PageResult<CommunitySectionPO> page(CommunitySectionPageRequest request) {
        Page<CommunitySectionPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        boolean defaultOrder = request.getSortingFields() == null || request.getSortingFields().isEmpty();
        LambdaQueryWrapper<CommunitySectionPO> wrapper = new LambdaQueryWrapper<CommunitySectionPO>()
                .like(StrUtil.isNotBlank(request.getName()), CommunitySectionPO::getName, request.getName())
                .like(StrUtil.isNotBlank(request.getCode()), CommunitySectionPO::getCode, request.getCode())
                .eq(request.getParentId() != null, CommunitySectionPO::getParentId, request.getParentId())
                .eq(request.getStatus() != null, CommunitySectionPO::getStatus, request.getStatus())
                .orderByAsc(defaultOrder, CommunitySectionPO::getSort)
                .orderByDesc(defaultOrder, CommunitySectionPO::getId);
        Page<CommunitySectionPO> result = communitySectionInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Transactional
    public CommunitySectionPO update(Long id, CommunitySectionUpdateRequest request) {
        CommunitySectionPO section = get(id);
        Long tenantId = SecurityHelper.getRequiredTenantId();
        String code = normalizeCode(request.getCode());
        ensureCodeUnique(code, id);
        validateParent(request.getParentId(), id, tenantId);
        applyMutableFields(section, request.getName(), code, request.getDescription(), request.getCoverUrl(), request.getParentId(), request.getSort(), request.getStatus());
        ApplicationAssert.requireSuccess(communitySectionInternalService.updateById(section), ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        CommunitySectionPO section = get(id);
        long children = communitySectionInternalService.count(new LambdaQueryWrapper<CommunitySectionPO>()
                .eq(CommunitySectionPO::getParentId, id));
        if (children > 0) {
            throw new BusinessException(ErrorCode.COMMUNITY_SECTION_HAS_CHILDREN);
        }
        ApplicationAssert.requireSuccess(communitySectionInternalService.removeById(id), ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
    }

    private void applyMutableFields(CommunitySectionPO section,
                                    String name,
                                    String code,
                                    String description,
                                    String coverUrl,
                                    Long parentId,
                                    Integer sort,
                                    StatusEnum status) {
        if (StrUtil.isBlank(name)) {
            ApplicationAssert.invalidParam("社区板块name不能为空");
        }
        section.setName(name.trim());
        section.setCode(code);
        section.setDescription(trimToNull(description));
        section.setCoverUrl(trimToNull(coverUrl));
        section.setParentId(parentId == null ? 0L : parentId);
        section.setSort(sort == null ? 0 : sort);
        section.setStatus(status == null ? StatusEnum.NORMAL : status);
    }

    private void ensureCodeUnique(String code, Long ignoredId) {
        long count = communitySectionInternalService.count(new LambdaQueryWrapper<CommunitySectionPO>()
                .eq(CommunitySectionPO::getCode, code)
                .ne(ignoredId != null, CommunitySectionPO::getId, ignoredId));
        if (count > 0) {
            throw new BusinessException(ErrorCode.COMMUNITY_SECTION_CODE_DUPLICATE);
        }
    }

    private void validateParent(Long parentId, Long selfId, Long tenantId) {
        if (parentId == null || parentId == 0L) {
            return;
        }
        if (parentId < 0) {
            ApplicationAssert.invalidParam("社区板块parentId不能小于0");
        }
        if (Objects.equals(parentId, selfId)) {
            ApplicationAssert.invalidParam("社区板块不能设置自身为父板块");
        }
        CommunitySectionPO parent = communitySectionInternalService.getById(parentId);
        if (parent == null || !Objects.equals(parent.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        }
    }

    private static void ensureCurrentTenant(CommunitySectionPO section, Long tenantId) {
        if (!Objects.equals(section.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        }
    }

    private static String normalizeCode(String code) {
        if (StrUtil.isBlank(code)) {
            ApplicationAssert.invalidParam("社区板块code不能为空");
        }
        return code.trim().toLowerCase(Locale.ROOT);
    }

    private static String trimToNull(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return value.trim();
    }
}
