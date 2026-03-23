package me.link.bootstrap.core.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "选项数据")
@Data
@Builder
public class SelectOptions {

    @Schema(description = "选项 ID", example = "1")
    private Long id;

    @Schema(description = "选项名称", example = "选项一")
    private String name;
}
