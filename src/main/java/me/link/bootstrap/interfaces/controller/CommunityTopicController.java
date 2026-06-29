package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.CommunityTopicApplicationService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.topic.CommunityTopicUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityTopicResponseVO;
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
 * 社区话题后台管理接口。
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "社区话题接口", description = "社区话题后台管理接口")
@RequestMapping(GlobalConstants.API_PREFIX + "/system/community/topics")
public class CommunityTopicController {

    private final CommunityTopicApplicationService communityTopicApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @Idempotent
    @PostMapping
    @SaCheckPermission("system:community:topic:create")
    @Operation(summary = "创建社区话题")
    public ResultResponse<CommunityTopicResponseVO> create(@Valid @RequestBody CommunityTopicCreateRequest request) {
        CommunityTopicPO topic = communityTopicApplicationService.create(request);
        return ResultResponse.success(responseVOConverter.toResponse(topic));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("system:community:topic:query")
    @Operation(summary = "获取社区话题详情")
    public ResultResponse<CommunityTopicResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        CommunityTopicPO topic = communityTopicApplicationService.get(id);
        return ResultResponse.success(responseVOConverter.toResponse(topic));
    }

    @GetMapping
    @SaCheckPermission("system:community:topic:list")
    @Operation(summary = "分页查询社区话题")
    public ResultTableResponse<CommunityTopicResponseVO> page(@Validated @SortWhitelist(CommunityTopicResponseVO.class) CommunityTopicPageRequest request) {
        PageResult<CommunityTopicPO> pageResult = communityTopicApplicationService.page(request);
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @Idempotent
    @PutMapping("/{id}")
    @SaCheckPermission("system:community:topic:update")
    @Operation(summary = "更新社区话题")
    public ResultResponse<CommunityTopicResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id, @Valid @RequestBody CommunityTopicUpdateRequest request) {
        CommunityTopicPO topic = communityTopicApplicationService.update(id, request);
        return ResultResponse.success(responseVOConverter.toResponse(topic));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("system:community:topic:delete")
    @Operation(summary = "删除社区话题")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        communityTopicApplicationService.delete(id);
        return ResultResponse.success();
    }
}
