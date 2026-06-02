package me.link.bootstrap.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * 统一表格响应对象，用于包装分页列表数据和总记录数。
 */
@Schema(description = "统一响应结果")
@Data
    public class ResultTableResponse<E> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    @Schema(description = "业务数据", example = "[{\"id\": 123, \"name\": \"example\"}]")
    private List<E> records;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "业务错误码（非HTTP状态码）", example = "401_000_001")
    private long code;

    @Schema(description = "服务端处理完成时间戳（ISO 8601）", example = "2026-05-19T15:46:22.123Z")
    private long timestamp;

    @Schema(description = "链路追踪ID（用于日志关联）", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
    private String traceId;

    @Schema(description = "当前列表支持排序的有效字段白名单")
    private List<String> sortableFields;


    private ResultTableResponse() {
    }

    private ResultTableResponse(final List<E> records, final Long total, final Long code, final long timestamp, final String traceId) {
        this.records = records;
        this.total = total;
        this.code = code;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }


    public static <E> ResultTableResponse<E> success(final List<E> records, final Long total) {
        return new ResultTableResponse<>(records, total, ErrorCode.SUCCESS.getCode(), Instant.now().toEpochMilli(), null);
    }

    public static <E> ResultTableResponse<E> failure(ErrorCode errorCode) {
        return new ResultTableResponse<>(null, 0L, errorCode.getCode(), Instant.now().toEpochMilli(), null);
    }
}
