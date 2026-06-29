package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.CommunitySectionApplicationService;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.CommunitySectionResponseVO;
import me.link.bootstrap.interfaces.validation.SortWhitelist;
import me.link.bootstrap.shared.kernel.annotation.Idempotent;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 社区板块后台管理接口。
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "社区板块接口", description = "社区板块后台管理接口")
@RequestMapping(GlobalConstants.API_PREFIX + "/system/community/sections")
public class CommunitySectionController {

    private final CommunitySectionApplicationService communitySectionApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @Idempotent
    @PostMapping
    @SaCheckPermission("system:community:section:create")
    @Operation(summary = "创建社区板块")
    public ResultResponse<CommunitySectionResponseVO> create(@Valid @RequestBody CommunitySectionCreateRequest request) {
        CommunitySectionPO section = communitySectionApplicationService.create(request);
        return ResultResponse.success(responseVOConverter.toResponse(section));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("system:community:section:query")
    @Operation(summary = "获取社区板块详情")
    public ResultResponse<CommunitySectionResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        CommunitySectionPO section = communitySectionApplicationService.get(id);
        return ResultResponse.success(responseVOConverter.toResponse(section));
    }

    @GetMapping
    @SaCheckPermission("system:community:section:list")
    @Operation(summary = "分页查询社区板块")
    public ResultTableResponse<CommunitySectionResponseVO> page(@Validated @SortWhitelist(CommunitySectionResponseVO.class) CommunitySectionPageRequest request) {
        PageResult<CommunitySectionPO> pageResult = communitySectionApplicationService.page(request);
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @Idempotent
    @PutMapping("/{id}")
    @SaCheckPermission("system:community:section:update")
    @Operation(summary = "更新社区板块")
    public ResultResponse<CommunitySectionResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id, @Valid @RequestBody CommunitySectionUpdateRequest request) {
        CommunitySectionPO section = communitySectionApplicationService.update(id, request);
        return ResultResponse.success(responseVOConverter.toResponse(section));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("system:community:section:delete")
    @Operation(summary = "删除社区板块")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        communitySectionApplicationService.delete(id);
        return ResultResponse.success();
    }
}
