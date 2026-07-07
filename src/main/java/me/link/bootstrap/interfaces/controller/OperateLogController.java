package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.OperateLogService;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogPageRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.OperateLogResponseVO;
import me.link.bootstrap.interfaces.validation.SortWhitelist;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志查询接口控制器。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/operate-log")
@Validated
@RequiredArgsConstructor
@Tag(name = "操作日志接口", description = "操作日志只读审计查询接口")
public class OperateLogController {

    private final OperateLogService operateLogService;

    @GetMapping("/{id}")
    @SaCheckPermission("system:operate-log:query")
    @Operation(summary = "查询操作日志详情", description = "根据ID查询操作日志详情")
    public ResultResponse<OperateLogResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(operateLogService.get(id));
    }

    @GetMapping
    @SaCheckPermission("system:operate-log:list")
    @Operation(summary = "分页查询操作日志", description = "分页查询操作日志列表")
    public ResultTableResponse<OperateLogResponseVO> page(@Validated @SortWhitelist(OperateLogResponseVO.class) OperateLogPageRequest request) {
        PageResult<OperateLogResponseVO> pageResult = operateLogService.page(request);
        return ResultTableResponse.success(pageResult.records(), pageResult.total());
    }

}
