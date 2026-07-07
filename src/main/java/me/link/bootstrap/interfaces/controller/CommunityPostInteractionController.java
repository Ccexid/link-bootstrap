package me.link.bootstrap.interfaces.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.CommunityPostInteractionService;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityPostInteractionResponseVO;
import me.link.bootstrap.shared.kernel.annotation.Idempotent;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 社区帖子互动用户端接口。
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "社区帖子互动接口", description = "社区帖子点赞和收藏接口")
@RequestMapping(GlobalConstants.API_PREFIX + "/community/posts")
public class CommunityPostInteractionController {

    private final CommunityPostInteractionService communityPostInteractionService;

    @Idempotent
    @PostMapping("/{id}/likes")
    @PreAuthorize("hasAuthority('community:post:like')")
    @Operation(summary = "点赞帖子")
    public ResultResponse<CommunityPostInteractionResponseVO> like(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(communityPostInteractionService.like(id));
    }

    @DeleteMapping("/{id}/likes")
    @PreAuthorize("hasAuthority('community:post:unlike')")
    @Operation(summary = "取消点赞帖子")
    public ResultResponse<CommunityPostInteractionResponseVO> unlike(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(communityPostInteractionService.unlike(id));
    }

    @Idempotent
    @PostMapping("/{id}/collections")
    @PreAuthorize("hasAuthority('community:post:collect')")
    @Operation(summary = "收藏帖子")
    public ResultResponse<CommunityPostInteractionResponseVO> collect(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(communityPostInteractionService.collect(id));
    }

    @DeleteMapping("/{id}/collections")
    @PreAuthorize("hasAuthority('community:post:uncollect')")
    @Operation(summary = "取消收藏帖子")
    public ResultResponse<CommunityPostInteractionResponseVO> uncollect(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(communityPostInteractionService.uncollect(id));
    }

    @GetMapping("/{id}/interactions/current")
    @Operation(summary = "查询当前用户帖子互动状态")
    public ResultResponse<CommunityPostInteractionResponseVO> interaction(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(communityPostInteractionService.interaction(id));
    }
}
