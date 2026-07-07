package me.link.bootstrap.interfaces.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.CommunitySectionService;
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

    private final CommunitySectionService communitySectionService;

    @Idempotent
    @PostMapping
    @PreAuthorize("hasAuthority('system:community:section:create')")
    @Operation(summary = "创建社区板块")
    public ResultResponse<CommunitySectionResponseVO> create(@Valid @RequestBody CommunitySectionCreateRequest request) {
        return ResultResponse.success(communitySectionService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:community:section:query')")
    @Operation(summary = "获取社区板块详情")
    public ResultResponse<CommunitySectionResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(communitySectionService.get(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:community:section:list')")
    @Operation(summary = "分页查询社区板块")
    public ResultTableResponse<CommunitySectionResponseVO> page(@Validated @SortWhitelist(CommunitySectionResponseVO.class) CommunitySectionPageRequest request) {
        PageResult<CommunitySectionResponseVO> pageResult = communitySectionService.page(request);
        return ResultTableResponse.success(pageResult.records(), pageResult.total());
    }

    @Idempotent
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:community:section:update')")
    @Operation(summary = "更新社区板块")
    public ResultResponse<CommunitySectionResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id, @Valid @RequestBody CommunitySectionUpdateRequest request) {
        return ResultResponse.success(communitySectionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:community:section:delete')")
    @Operation(summary = "删除社区板块")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        communitySectionService.delete(id);
        return ResultResponse.success();
    }
}
