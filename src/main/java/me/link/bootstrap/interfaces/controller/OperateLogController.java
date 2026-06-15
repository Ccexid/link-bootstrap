package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateOperateLogCommand;
import me.link.bootstrap.application.command.OperateLogPageQuery;
import me.link.bootstrap.application.command.UpdateOperateLogCommand;
import me.link.bootstrap.application.service.OperateLogApplicationService;
import me.link.bootstrap.domain.entity.OperateLogEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogCreateRequest;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogPageRequest;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.OperateLogResponseVO;
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

/**
 * 操作日志接口控制器，对外提供操作日志增删改查 REST 接口。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/operate-log")
@Validated
@RequiredArgsConstructor
@Tag(name = "操作日志接口", description = "操作日志增删改查接口")
public class OperateLogController {

    private final OperateLogApplicationService operateLogApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @PostMapping
    @SaCheckPermission("system:operate-log:create")
    @Operation(summary = "创建操作日志", description = "创建操作日志基础信息")
    public ResultResponse<OperateLogResponseVO> create(@Valid @RequestBody OperateLogCreateRequest request) {
        OperateLogEntity operateLog = operateLogApplicationService.create(new CreateOperateLogCommand(
                request.getTraceId(),
                request.getUserId(),
                request.getUserType(),
                request.getUserIp(),
                request.getUserAgent(),
                request.getModule(),
                request.getOperation(),
                request.getBizId(),
                request.getAction(),
                request.getExtra(),
                request.getSuccess(),
                request.getRequestMethod(),
                request.getRequestUrl(),
                request.getDuration()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(operateLog));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询操作日志详情", description = "根据ID查询操作日志详情")
    public ResultResponse<OperateLogResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(responseVOConverter.toResponse(operateLogApplicationService.get(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询操作日志", description = "分页查询操作日志列表")
    public ResultTableResponse<OperateLogResponseVO> page(@Validated @SortWhitelist(OperateLogResponseVO.class) OperateLogPageRequest request) {
        PageResult<OperateLogEntity> pageResult = operateLogApplicationService.page(new OperateLogPageQuery(
                request.getPageNo(),
                request.getPageSize(),
                request.getTraceId(),
                request.getUserId(),
                request.getModule(),
                request.getOperation(),
                request.getBizId(),
                request.getSuccess(),
                request.getSortingFields()
        ));
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("system:operate-log:update")
    @Operation(summary = "更新操作日志", description = "更新操作日志基础信息")
    public ResultResponse<OperateLogResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody OperateLogUpdateRequest request) {
        OperateLogEntity operateLog = operateLogApplicationService.update(new UpdateOperateLogCommand(
                id,
                request.getTraceId(),
                request.getUserId(),
                request.getUserType(),
                request.getUserIp(),
                request.getUserAgent(),
                request.getModule(),
                request.getOperation(),
                request.getBizId(),
                request.getAction(),
                request.getExtra(),
                request.getSuccess(),
                request.getRequestMethod(),
                request.getRequestUrl(),
                request.getDuration()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(operateLog));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("system:operate-log:delete")
    @Operation(summary = "删除操作日志", description = "根据ID删除操作日志")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        operateLogApplicationService.delete(id);
        return ResultResponse.success();
    }

}
