package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 社区帖子互动状态响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "社区帖子互动状态响应")
public class CommunityPostInteractionResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否已点赞")
    private Boolean liked;

    @Schema(description = "是否已收藏")
    private Boolean collected;

    @Schema(description = "点赞数")
    private Long likeCount;

    @Schema(description = "收藏数")
    private Long collectCount;
}
