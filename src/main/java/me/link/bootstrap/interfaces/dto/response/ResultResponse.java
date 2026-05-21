package me.link.bootstrap.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.infrastructure.tracing.TraceIdContext;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Schema(description = "统一响应结果")
@Data
public class ResultResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "业务数据", example = "{\"id\": 123, \"name\": \"example\"}")
    private T data;

    @Schema(description = "用户可读提示消息", example = "操作成功")
    private String message;

    @Schema(description = "业务错误码（非HTTP状态码）", example = "401_000_001")
    private long code;

    @Schema(description = "服务端处理完成时间戳（ISO 8601）", example = "2026-05-19T15:46:22.123Z")
    private long timestamp;

    @Schema(description = "链路追踪ID（用于日志关联）", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
    private String traceId;


    private ResultResponse() {
    }

    private ResultResponse(final T data, final String message, final Long code, final long timestamp, final String traceId) {
        this.data = data;
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    private static <T> ResultResponse<T> of(final T data, final String message, final Long code) {
        return new ResultResponse<>(data, message, code, Instant.now().toEpochMilli(), TraceIdContext.get());
    }

    /**
     * 创建成功响应(无数据)
     *
     * @param <T> 数据类型泛型参数
     * @return 仅包含成功状态码和默认消息的ResultResponse对象
     */
    public static <T> ResultResponse<T> success() {
        return of(null, ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    /**
     * 创建成功响应(携带业务数据)
     *
     * @param data 响应数据，泛型类型，可以是任意对象
     * @param <T>  数据类型泛型参数
     * @return 包含数据和成功状态码的ResultResponse对象
     */
    public static <T> ResultResponse<T> success(T data) {
        return of(data, ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    /**
     * 创建成功响应(自定义提示消息)
     *
     * @param message 用户可读的提示消息
     * @param <T>     数据类型泛型参数
     * @return 包含自定义消息和成功状态码的ResultResponse对象
     */
    public static <T> ResultResponse<T> successWithMessage(String message) {
        return of(null, message, ErrorCode.SUCCESS.getCode());
    }

    /**
     * 创建成功响应(携带数据和自定义消息)
     *
     * @param data    响应数据，泛型类型
     * @param message 用户可读的提示消息
     * @param <T>     数据类型泛型参数
     * @return 包含数据和自定义消息的ResultResponse对象
     */
    public static <T> ResultResponse<T> success(T data, String message) {
        return of(data, message, ErrorCode.SUCCESS.getCode());
    }

    /**
     * 创建失败响应(使用错误码默认消息)
     *
     * @param errorCode 业务错误码枚举
     * @param <T>       数据类型泛型参数
     * @return 包含错误码和默认错误消息的ResultResponse对象
     */
    public static <T> ResultResponse<T> failure(ErrorCode errorCode) {
        return of(null, errorCode.getMessage(), errorCode.getCode());
    }

    /**
     * 创建失败响应(自定义错误消息)
     *
     * @param errorCode 业务错误码枚举
     * @param message   自定义错误提示消息，覆盖errorCode中的默认消息
     * @param <T>       数据类型泛型参数
     * @return 包含错误码和自定义错误消息的ResultResponse对象
     */
    public static <T> ResultResponse<T> failure(ErrorCode errorCode, String message) {
        return of(null, message, errorCode.getCode());
    }
}
