package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.infrastructure.persistence.internal.CommunitySectionInternalService;
import me.link.bootstrap.infrastructure.persistence.internal.CommunityTopicInternalService;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicUpdateRequest;
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
 * 社区话题服务，负责话题校验、分页查询和持久化编排。
 */
@Service
@RequiredArgsConstructor
public class CommunityTopicApplicationService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "section_id", "section_id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "code", "code",
            "sort", "sort"
    );

    private final CommunityTopicInternalService communityTopicInternalService;
    private final CommunitySectionInternalService communitySectionInternalService;

    @Transactional
    public CommunityTopicPO create(CommunityTopicCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateSection(request.getSectionId(), tenantId);
        String code = normalizeCode(request.getCode());
        ensureCodeUnique(code, null);
        CommunityTopicPO topic = new CommunityTopicPO();
        applyMutableFields(topic, request.getSectionId(), request.getName(), code, request.getDescription(),
                request.getCoverUrl(), request.getSort(), request.getStatus());
        topic.setTenantId(tenantId);
        communityTopicInternalService.save(topic);
        return topic;
    }

    public CommunityTopicPO get(Long id) {
        CommunityTopicPO topic = ApplicationAssert.requireFound(communityTopicInternalService.getById(id), ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
        ensureCurrentTenant(topic, SecurityHelper.getRequiredTenantId());
        return topic;
    }

    public PageResult<CommunityTopicPO> page(CommunityTopicPageRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        if (request.getSectionId() != null) {
            validateSection(request.getSectionId(), tenantId);
        }
        Page<CommunityTopicPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        boolean defaultOrder = request.getSortingFields() == null || request.getSortingFields().isEmpty();
        LambdaQueryWrapper<CommunityTopicPO> wrapper = new LambdaQueryWrapper<CommunityTopicPO>()
                .eq(request.getSectionId() != null, CommunityTopicPO::getSectionId, request.getSectionId())
                .like(StrUtil.isNotBlank(request.getName()), CommunityTopicPO::getName, request.getName())
                .like(StrUtil.isNotBlank(request.getCode()), CommunityTopicPO::getCode, request.getCode())
                .eq(request.getStatus() != null, CommunityTopicPO::getStatus, request.getStatus())
                .orderByAsc(defaultOrder, CommunityTopicPO::getSort)
                .orderByDesc(defaultOrder, CommunityTopicPO::getId);
        Page<CommunityTopicPO> result = communityTopicInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Transactional
    public CommunityTopicPO update(Long id, CommunityTopicUpdateRequest request) {
        CommunityTopicPO topic = get(id);
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateSection(request.getSectionId(), tenantId);
        String code = normalizeCode(request.getCode());
        ensureCodeUnique(code, id);
        applyMutableFields(topic, request.getSectionId(), request.getName(), code, request.getDescription(),
                request.getCoverUrl(), request.getSort(), request.getStatus());
        ApplicationAssert.requireSuccess(communityTopicInternalService.updateById(topic), ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        CommunityTopicPO topic = get(id);
        ApplicationAssert.requireSuccess(communityTopicInternalService.removeById(id), ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
    }

    private void applyMutableFields(CommunityTopicPO topic,
                                    Long sectionId,
                                    String name,
                                    String code,
                                    String description,
                                    String coverUrl,
                                    Integer sort,
                                    StatusEnum status) {
        if (StrUtil.isBlank(name)) {
            ApplicationAssert.invalidParam("社区话题name不能为空");
        }
        topic.setSectionId(sectionId);
        topic.setName(name.trim());
        topic.setCode(code);
        topic.setDescription(trimToNull(description));
        topic.setCoverUrl(trimToNull(coverUrl));
        topic.setSort(sort == null ? 0 : sort);
        topic.setStatus(status == null ? StatusEnum.NORMAL : status);
    }

    private void validateSection(Long sectionId, Long tenantId) {
        if (sectionId == null || sectionId <= 0) {
            ApplicationAssert.invalidParam("社区话题sectionId必须大于0");
        }
        CommunitySectionPO section = communitySectionInternalService.getById(sectionId);
        if (section == null || !Objects.equals(section.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        }
    }

    private void ensureCodeUnique(String code, Long ignoredId) {
        long count = communityTopicInternalService.count(new LambdaQueryWrapper<CommunityTopicPO>()
                .eq(CommunityTopicPO::getCode, code)
                .ne(ignoredId != null, CommunityTopicPO::getId, ignoredId));
        if (count > 0) {
            throw new BusinessException(ErrorCode.COMMUNITY_TOPIC_CODE_DUPLICATE);
        }
    }

    private static void ensureCurrentTenant(CommunityTopicPO topic, Long tenantId) {
        if (!Objects.equals(topic.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
        }
    }

    private static String normalizeCode(String code) {
        if (StrUtil.isBlank(code)) {
            ApplicationAssert.invalidParam("社区话题code不能为空");
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
