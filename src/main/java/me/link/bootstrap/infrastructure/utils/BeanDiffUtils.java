package me.link.bootstrap.infrastructure.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.annotation.LogField;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean 差异比较工具类 (DDD 基础设施层)
 * 优化点：加入字段缓存、支持脱敏、日期格式化及空值友好处理
 */
@Slf4j
public class BeanDiffUtils {

    // 缓存类的字段信息，避免频繁反射损耗性能
    // 使用 ConcurrentHashMap 保证多线程环境下的线程安全
    private static final Map<Class<?>, Field[]> FIELDS_CACHE = new ConcurrentHashMap<>();

    /**
     * 比较两个对象之间的差异
     *
     * @param oldObj 旧对象
     * @param newObj 新对象
     * @return 包含差异详情的列表，每个元素是一个 Map，包含字段名、字段标签、修改前后的值
     */
    public static List<Map<String, Object>> diff(Object oldObj, Object newObj) {
        // 步骤 1: 空值检查
        // 如果任一对象为 null，无法进行比较，直接返回空列表
        if (oldObj == null || newObj == null) {
            return Collections.emptyList();
        }

        // 步骤 2: 类型一致性检查
        // 确保两个对象的类类型完全一致，否则记录警告并返回空列表
        if (!oldObj.getClass().equals(newObj.getClass())) {
            log.warn("BeanDiffUtils: Objects to compare have different types.");
            return Collections.emptyList();
        }

        // 步骤 3: 初始化结果列表
        List<Map<String, Object>> changeDetails = new ArrayList<>();

        // 步骤 4: 获取字段列表（带缓存机制）
        // 从缓存中获取该类的字段数组，若不存在则通过反射获取并存入缓存
        Field[] fields = FIELDS_CACHE.computeIfAbsent(oldObj.getClass(), ReflectUtil::getFields);

        // 步骤 5: 遍历所有字段进行对比
        for (Field field : fields) {
            // 获取字段上的 @LogField 注解
            LogField logField = field.getAnnotation(LogField.class);
            
            // 如果字段没有 @LogField 注解，说明不需要记录日志，跳过该字段
            if (logField == null) {
                continue;
            }

            try {
                // 步骤 6: 获取字段值
                // 使用 Hutool 的反射工具获取旧对象和新对象中该字段的值
                Object oldValue = ReflectUtil.getFieldValue(oldObj, field);
                Object newValue = ReflectUtil.getFieldValue(newObj, field);

                // 步骤 7: 判断值是否发生变化
                // 使用 Hutool 的 ObjectUtil.notEqual 进行严谨比较（处理基本类型与包装类的差异）
                if (ObjectUtil.notEqual(oldValue, newValue)) {
                    // 构建差异详情 Map
                    Map<String, Object> detail = new HashMap<>(4);
                    detail.put("fieldLabel", logField.value());       // 放入字段中文标签
                    detail.put("fieldName", field.getName());         // 放入字段英文名
                    detail.put("beforeValue", formatValue(oldValue, logField)); // 放入格式化后的旧值
                    detail.put("afterValue", formatValue(newValue, logField));  // 放入格式化后的新值

                    // 将差异详情添加到结果列表
                    changeDetails.add(detail);
                }
            } catch (Exception e) {
                // 步骤 8: 异常处理
                // 捕获反射或处理过程中的异常，记录错误日志，避免中断整个比较流程
                log.error("BeanDiffUtils error comparing field: {}", field.getName(), e);
            }
        }
        
        // 步骤 9: 返回最终结果
        return changeDetails;
    }

    /**
     * 根据 @LogField 注解配置格式化输出值
     * 处理逻辑包括：空值处理、脱敏、日期格式化、字典标记
     *
     * @param value 原始值
     * @param ann   @LogField 注解实例
     * @return 格式化后的字符串
     */
    private static String formatValue(Object value, LogField ann) {
        // 步骤 1: 空值处理
        // 如果值为 null，直接返回空字符串，避免后续抛出空指针异常
        if (value == null) {
            return "";
        }

        // 步骤 2: 脱敏处理
        // 如果注解标记为敏感字段 (isSensitive=true)，则返回掩码字符串
        if (ann.isSensitive()) {
            return "******";
        }

        // 步骤 3: 日期格式化
        // 如果注解中指定了日期格式 (dateFormat 不为空)，尝试对日期类型进行格式化
        if (StringUtils.isNotBlank(ann.dateFormat())) {
            if (value instanceof Date) {
                // 处理 java.util.Date 类型
                return DateUtil.format((Date) value, ann.dateFormat());
            } else if (value instanceof LocalDateTime) {
                // 处理 java.time.LocalDateTime 类型
                return DateUtil.format((LocalDateTime) value, ann.dateFormat());
            }
        }

        // 步骤 4: 字典翻译标记
        // 如果注解中指定了字典类型 (dictType 不为空)，在此处添加标记前缀
        // 注意：实际的字典文本翻译通常需要在 Application 层注入 DictService 完成，此处仅作标识
        if (StringUtils.isNotBlank(ann.dictType())) {
            // return dictService.getLabel(ann.dictType(), value.toString()); // 如需真实翻译可取消注释并注入服务
            return String.format("[%s]%s", ann.dictType(), value);
        }

        // 步骤 5: 默认处理
        // 如果以上条件都不满足，直接调用 toString() 返回字符串形式
        return String.valueOf(value);
    }
}