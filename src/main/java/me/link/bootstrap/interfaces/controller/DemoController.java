package me.link.bootstrap.interfaces.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/demo")
@Validated
@RequiredArgsConstructor
@Tag(name = "Demo接口")
@Slf4j
public class DemoController {

    @GetMapping
    public ResultResponse<Void> demo() {
        log.info("DemoController.demo()");
        return ResultResponse.success();
    }
}
