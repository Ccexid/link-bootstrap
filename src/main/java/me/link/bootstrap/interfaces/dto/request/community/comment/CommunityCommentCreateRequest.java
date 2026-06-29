package me.link.bootstrap.interfaces.dto.request.community.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 社区评论创建请求。
 */
@Data
@Schema(description = "社区评论创建请求")
public class CommunityCommentCreateRequest implements Serializable {

    @NotNull(message = "帖子ID不能为空")
    @Min(value = 1, message = "帖子ID必须大于0")
    @Schema(description = "帖子ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;

    @Min(value = 0, message = "父评论ID不能小于0")
    @Schema(description = "父评论ID，0或空表示一级评论")
    private Long parentId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容长度不能超过2000个字符")
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
