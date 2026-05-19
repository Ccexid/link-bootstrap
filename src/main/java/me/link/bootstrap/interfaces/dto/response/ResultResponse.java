package me.link.bootstrap.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.infrastructure.tracing.TraceIdContext;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "统一响应结果")
@Data
public class ResultResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "业务数据", example = "{\"id\": 123, \"name\": \"example\"}")
    private T data;
    @Schema(description = "用户可读提示消息", example = "操作成功")
    private String message;

    @Schema(description = "业务错误码（非HTTP状态码）", example = "USER_404_001")
    private String code;

    @Schema(description = "链路追踪ID（用于日志关联）", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
    private String traceId;


    private ResultResponse() {
    }

    private ResultResponse(final T data, final String message, final String code, final String traceId) {
        this.data = data;
        this.message = message;
        this.code = code;
        this.traceId = traceId;
    }

    /**
     * 创建成功响应结果
     *
     * @param data 响应数据，泛型类型，可以是任意对象
     * @param <T>  数据类型泛型参数
     * @return 包含数据和成功状态码(200)的ResultResponse对象
     */
    public static <T> ResultResponse<T> success(final T data) {
        return new ResultResponse<>(data, "操作成功", "200", TraceIdContext.get());
    }

}
