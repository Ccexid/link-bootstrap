package me.link.bootstrap.interfaces.dto.request.community.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 社区评论更新请求。
 */
@Data
@Schema(description = "社区评论更新请求")
public class CommunityCommentUpdateRequest implements Serializable {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容长度不能超过2000个字符")
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
