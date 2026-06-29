package me.link.bootstrap.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.CommunityCommentApplicationService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityCommentPO;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityCommentResponseVO;
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
 * 社区评论用户端接口。
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "社区评论接口", description = "社区评论用户端接口")
@RequestMapping(GlobalConstants.API_PREFIX + "/community/comments")
public class CommunityCommentController {

    private final CommunityCommentApplicationService communityCommentApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @Idempotent
    @PostMapping
    @Operation(summary = "创建社区评论或回复")
    public ResultResponse<CommunityCommentResponseVO> create(@Valid @RequestBody CommunityCommentCreateRequest request) {
        CommunityCommentPO comment = communityCommentApplicationService.create(request);
        return ResultResponse.success(responseVOConverter.toResponse(comment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取社区评论详情")
    public ResultResponse<CommunityCommentResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        CommunityCommentPO comment = communityCommentApplicationService.get(id);
        return ResultResponse.success(responseVOConverter.toResponse(comment));
    }

    @GetMapping
    @Operation(summary = "分页查询社区评论")
    public ResultTableResponse<CommunityCommentResponseVO> page(@Validated @SortWhitelist(CommunityCommentResponseVO.class) CommunityCommentPageRequest request) {
        PageResult<CommunityCommentPO> pageResult = communityCommentApplicationService.page(request);
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @Idempotent
    @PutMapping("/{id}")
    @Operation(summary = "更新本人社区评论")
    public ResultResponse<CommunityCommentResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id, @Valid @RequestBody CommunityCommentUpdateRequest request) {
        CommunityCommentPO comment = communityCommentApplicationService.update(id, request);
        return ResultResponse.success(responseVOConverter.toResponse(comment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除本人社区评论")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        communityCommentApplicationService.delete(id);
        return ResultResponse.success();
    }
}
