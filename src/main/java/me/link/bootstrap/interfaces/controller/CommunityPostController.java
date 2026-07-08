package me.link.bootstrap.interfaces.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.CommunityPostService;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.post.CommunityPostUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityPostResponseVO;
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
 * 社区帖子用户端接口。
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "社区帖子接口", description = "社区帖子用户端接口")
@RequestMapping(GlobalConstants.API_PREFIX + "/community/posts")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    /**
     * 创建社区帖子。
     */
    @Idempotent
    @PostMapping
    @PreAuthorize("hasAuthority('community:post:create')")
    @Operation(summary = "创建社区帖子")
    public ResultResponse<CommunityPostResponseVO> create(@Valid @RequestBody CommunityPostCreateRequest request) {
        return ResultResponse.success(communityPostService.create(request));
    }

    /**
     * 获取社区帖子详情。
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取社区帖子详情")
    public ResultResponse<CommunityPostResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(communityPostService.get(id));
    }

    /**
     * 分页查询社区帖子。
     */
    @GetMapping
    @Operation(summary = "分页查询社区帖子")
    public ResultTableResponse<CommunityPostResponseVO> page(@Validated @SortWhitelist(CommunityPostResponseVO.class) CommunityPostPageRequest request) {
        PageResult<CommunityPostResponseVO> pageResult = communityPostService.page(request);
        return ResultTableResponse.success(pageResult.records(), pageResult.total());
    }

    /**
     * 更新本人社区帖子。
     */
    @Idempotent
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('community:post:update')")
    @Operation(summary = "更新本人社区帖子")
    public ResultResponse<CommunityPostResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id, @Valid @RequestBody CommunityPostUpdateRequest request) {
        return ResultResponse.success(communityPostService.update(id, request));
    }

    /**
     * 删除本人社区帖子。
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('community:post:delete')")
    @Operation(summary = "删除本人社区帖子")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        communityPostService.delete(id);
        return ResultResponse.success();
    }
}
