package me.link.bootstrap.interfaces.dto.request;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 可排序的分页参数
 * <p>
 * 继承自 {@link PageRequest}，额外支持通过 {@code sort} 字段指定排序规则。
 * 排序格式示例：{@code -field1,field2} 表示按 field1 降序，field2 升序。
 * </p>
 * <p>
 * <strong>安全提示：</strong>字段名只能包含字母、数字、下划线和点号，
 * 单个字段名最长 100 字符。
 * </p>
 *
 * @author Ccexid
 */
@Schema(description = "可排序的分页参数")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SortablePageRequest extends PageRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 排序 descending 前缀标记
     */
    private static final String DESC_PREFIX = "-";

    /**
     * 字段名最大长度
     */
    private static final int FIELD_MAX_LENGTH = 100;

    /**
     * 字段名校验正则表达式
     */
    private static final Pattern FIELD_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    /**
     * 排序字段字符串
     * <p>
     * 格式说明：
     * <ul>
     *   <li>多个字段使用逗号分隔，例如：{@code field1,-field2,field3}</li>
     *   <li>字段前加 {@code -} 表示降序排列，不加则表示升序排列</li>
     *   <li>示例：{@code -createTime,id} 表示先按创建时间降序，再按 ID 升序</li>
     *   <li>字段名只能包含字母、数字、下划线和点号，最长 100 字符</li>
     * </ul>
     */
    @Schema(description = "排序字段，格式：-field1,field2", example = "-createTime,id")
    private String sort;

    /**
     * 自动解析 {@code sort} 字符串为排序对象列表
     * <p>
     * 解析规则：
     * <ol>
     *   <li>若 {@code sort} 为空或空白，返回空列表</li>
     *   <li>按逗号分割字符串，遍历每个字段</li>
     *   <li>若字段以 {@code -} 开头，则创建降序的 {@link SortingField} 对象</li>
     *   <li>否则，创建升序的 {@link SortingField} 对象</li>
     *   <li>跳过非法字段名（格式不符或超长）并记录警告日志</li>
     * </ol>
     *
     * @return 排序字段对象列表，若未指定排序或所有字段均无效则返回空列表
     */
    public List<SortingField> getSortingFields() {
        if (StrUtil.isBlank(sort)) {
            return Collections.emptyList();
        }

        return Arrays.stream(sort.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::parseSortingField)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 解析单个排序字段
     *
     * @param fieldStr 原始字段名字符串
     * @return 解析后的 SortingField 对象，解析失败返回 null
     */
    private SortingField parseSortingField(String fieldStr) {
        boolean ascending = !fieldStr.startsWith(DESC_PREFIX);
        String fieldName = ascending ? fieldStr : fieldStr.substring(1);

        // 去除首尾空格后再次检查
        fieldName = fieldName.trim();

        // 验证字段名不为空
        if (fieldName.isEmpty()) {
            return null;
        }

        // 验证字段名长度
        if (fieldName.length() > FIELD_MAX_LENGTH) {
            return null;
        }

        // 验证字段名格式
        if (!FIELD_PATTERN.matcher(fieldName).matches()) {
            return null;
        }

        return new SortingField(fieldName, ascending);
    }
}
