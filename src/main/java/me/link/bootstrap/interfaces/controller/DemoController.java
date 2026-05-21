package me.link.bootstrap.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.TenantResponseVO;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/demo")
@Validated
@RequiredArgsConstructor
@Tag(name = "Demo接口", description = "演示测试接口")
@Slf4j
public class DemoController {

    @GetMapping
    @Operation(summary = "查询列表", description = "分页查询演示数据列表")
    public ResultTableResponse<TenantResponseVO> demo(@Validated SortablePageRequest pageRequest) {
        log.info("DemoController.demo()");
        List<TenantResponseVO> list = List.of(new TenantResponseVO());
        return ResultTableResponse.success(list, 10L);
    }
}
