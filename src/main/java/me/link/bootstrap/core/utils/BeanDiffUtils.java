package me.link.bootstrap.core.utils;

import cn.hutool.core.util.ObjectUtil;
import me.link.bootstrap.core.log.annotation.LogField;
import me.link.bootstrap.core.log.model.FieldChangeDetail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean 差异比较工具类
 * 用于比较两个对象中带有 @LogField 注解的字段变化，并生成变更详情列表
 */
public class BeanDiffUtils {

    /**
     * 比较新旧两个对象，提取带有日志注解的字段变更详情
     *
     * @param oldObj 旧对象（变更前）
     * @param newObj 新对象（变更后）
     * @return 包含字段变更详情的列表，若无变更或输入为空则返回空列表
     */
    public static List<FieldChangeDetail> diff(Object oldObj, Object newObj) {
        // 1. 初始化结果列表，用于存储检测到的字段变更详情
        List<FieldChangeDetail> details = new ArrayList<>();

        // 2. 前置校验：如果任一对象为 null，无法进行比较，直接返回空列表
        if (oldObj == null || newObj == null) {
            return details;
        }

        // 3. 获取旧对象声明的所有字段（不包括父类字段）
        Field[] fields = oldObj.getClass().getDeclaredFields();

        // 4. 遍历所有字段，筛选出需要记录日志的字段
        for (Field field : fields) {
            // 4.1 检查字段是否添加了 @LogField 注解，只有带此注解的字段才参与比较
            if (field.isAnnotationPresent(LogField.class)) {
                // 4.2 设置字段可访问，以绕过 Java 的访问控制检查（如 private 字段）
                field.setAccessible(true);
                try {
                    // 4.3 反射获取旧对象和新对象中该字段的值
                    Object oldValue = field.get(oldObj);
                    Object newValue = field.get(newObj);

                    // 4.4 使用 Hutool 工具类判断新旧值是否不相等
                    if (ObjectUtil.notEqual(oldValue, newValue)) {
                        // 4.5 获取字段上的 @LogField 注解实例，用于提取字段的中文描述等信息
                        LogField ann = field.getAnnotation(LogField.class);

                        // 4.6 构建变更详情对象：字段描述、旧值字符串、新值字符串，并加入结果列表
                        details.add(new FieldChangeDetail(
                                ann.value(),                 // 字段的业务名称（来自注解）
                                String.valueOf(oldValue),    // 旧值转为字符串
                                String.valueOf(newValue)     // 新值转为字符串
                        ));
                    }
                } catch (IllegalAccessException e) {
                    // 4.7 捕获反射访问异常（理论上不会发生，因为已 setAccessible(true)），忽略处理
                    // 实际生产中可考虑记录日志以便排查
                }
            }
        }

        // 5. 返回所有检测到的字段变更详情列表
        return details;
    }
}