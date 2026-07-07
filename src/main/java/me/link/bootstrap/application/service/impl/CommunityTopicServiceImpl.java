package me.link.bootstrap.application.service.impl;

import me.link.bootstrap.application.service.CommunityTopicService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.mapper.CommunityTopicMapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.application.service.CommunitySectionService;
import me.link.bootstrap.application.service.CommunityTopicService;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityTopicResponseVO;
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
public class CommunityTopicServiceImpl extends ServiceImpl<CommunityTopicMapper, CommunityTopicPO> implements CommunityTopicService {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "section_id", "section_id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "code", "code",
            "sort", "sort"
    );
    private final CommunitySectionService communitySectionService;

    @Transactional
    public CommunityTopicResponseVO create(CommunityTopicCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateSection(request.getSectionId(), tenantId);
        String code = normalizeCode(request.getCode());
        ensureCodeUnique(code, null);
        CommunityTopicPO topic = new CommunityTopicPO();
        applyMutableFields(topic, request.getSectionId(), request.getName(), code, request.getDescription(),
                request.getCoverUrl(), request.getSort(), request.getStatus());
        topic.setTenantId(tenantId);
        save(topic);
        return toResponse(topic);
    }

    public CommunityTopicResponseVO get(Long id) {
        CommunityTopicPO topic = ApplicationAssert.requireFound(getById(id), ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
        ensureCurrentTenant(topic, SecurityHelper.getRequiredTenantId());
        return toResponse(topic);
    }

    public PageResult<CommunityTopicResponseVO> page(CommunityTopicPageRequest request) {
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
        Page<CommunityTopicPO> result = page(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toResponse).toList(), result.getTotal());
    }

    @Transactional
    public CommunityTopicResponseVO update(Long id, CommunityTopicUpdateRequest request) {
        CommunityTopicPO topic = getRequired(id);
        Long tenantId = SecurityHelper.getRequiredTenantId();
        validateSection(request.getSectionId(), tenantId);
        String code = normalizeCode(request.getCode());
        ensureCodeUnique(code, id);
        applyMutableFields(topic, request.getSectionId(), request.getName(), code, request.getDescription(),
                request.getCoverUrl(), request.getSort(), request.getStatus());
        ApplicationAssert.requireSuccess(updateById(topic), ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        CommunityTopicPO topic = getRequired(id);
        ApplicationAssert.requireSuccess(removeById(id), ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
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
        CommunitySectionPO section = communitySectionService.getById(sectionId);
        if (section == null || !Objects.equals(section.getTenantId(), tenantId)) {
            throw new BusinessException(ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        }
    }

    private void ensureCodeUnique(String code, Long ignoredId) {
        long count = count(new LambdaQueryWrapper<CommunityTopicPO>()
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

    private CommunityTopicPO getRequired(Long id) {
        return ApplicationAssert.requireFound(getById(id), ErrorCode.COMMUNITY_TOPIC_NOT_FOUND);
    }

    private CommunityTopicResponseVO toResponse(CommunityTopicPO source) {
        CommunityTopicResponseVO response = BeanUtil.copyProperties(source, CommunityTopicResponseVO.class);
        response.setCreatedAt(source.getCreateTime());
        response.setUpdatedAt(source.getUpdateTime());
        return response;
    }
}
