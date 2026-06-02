package me.link.bootstrap.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateOrganizationCommand;
import me.link.bootstrap.application.command.OrganizationPageQuery;
import me.link.bootstrap.application.command.UpdateOrganizationCommand;
import me.link.bootstrap.application.service.OrganizationApplicationService;
import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationCreateRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationPageRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.OrganizationResponseVO;
import me.link.bootstrap.interfaces.validation.SortWhitelist;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/organization")
@Validated
@RequiredArgsConstructor
@Tag(name = "组织接口", description = "组织增删改查接口")
public class OrganizationController {

    private final OrganizationApplicationService organizationApplicationService;

    @PostMapping
    @Operation(summary = "创建组织", description = "创建组织基础信息")
    public ResultResponse<OrganizationResponseVO> create(@Valid @RequestBody OrganizationCreateRequest request) {
        OrganizationEntity organization = organizationApplicationService.create(new CreateOrganizationCommand(
                request.getName(),
                request.getOrgType(),
                request.getParentId(),
                request.getAncestors(),
                request.getLevel(),
                request.getContactName(),
                request.getContactMobile(),
                request.getStatus(),
                request.getTenantId()
        ));
        return ResultResponse.success(toResponse(organization));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询组织详情", description = "根据ID查询组织详情")
    public ResultResponse<OrganizationResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(toResponse(organizationApplicationService.get(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询组织", description = "分页查询组织列表")
    public ResultTableResponse<OrganizationResponseVO> page(@Validated @SortWhitelist(OrganizationResponseVO.class) OrganizationPageRequest request) {
        PageResult<OrganizationEntity> pageResult = organizationApplicationService.page(new OrganizationPageQuery(
                request.getPageNo(),
                request.getPageSize(),
                request.getName(),
                request.getOrgType(),
                request.getParentId(),
                request.getStatus(),
                request.getTenantId(),
                request.getSortingFields()
        ));
        List<OrganizationResponseVO> records = pageResult.records().stream()
                .map(this::toResponse)
                .toList();
        return ResultTableResponse.success(records, pageResult.total());
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新组织", description = "更新组织基础信息")
    public ResultResponse<OrganizationResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody OrganizationUpdateRequest request) {
        OrganizationEntity organization = organizationApplicationService.update(new UpdateOrganizationCommand(
                id,
                request.getName(),
                request.getOrgType(),
                request.getParentId(),
                request.getAncestors(),
                request.getLevel(),
                request.getContactName(),
                request.getContactMobile(),
                request.getStatus(),
                request.getTenantId()
        ));
        return ResultResponse.success(toResponse(organization));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除组织", description = "根据ID删除组织")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        organizationApplicationService.delete(id);
        return ResultResponse.success();
    }

    private OrganizationResponseVO toResponse(OrganizationEntity organization) {
        OrganizationResponseVO response = new OrganizationResponseVO();
        response.setId(organization.getId());
        response.setName(organization.getName());
        response.setOrgType(organization.getOrgType());
        response.setParentId(organization.getParentId());
        response.setAncestors(organization.getAncestors());
        response.setLevel(organization.getLevel());
        response.setContactName(organization.getContactName());
        response.setContactMobile(organization.getContactMobile());
        response.setStatus(organization.getStatus());
        response.setTenantId(organization.getTenantId());
        response.setCreatedAt(organization.getCreatedAt());
        response.setUpdatedAt(organization.getUpdatedAt());
        return response;
    }
}
