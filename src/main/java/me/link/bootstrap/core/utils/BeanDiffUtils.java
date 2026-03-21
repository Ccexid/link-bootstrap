package me.link.bootstrap.core.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import me.link.bootstrap.core.log.model.FieldChangeDetail;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Bean 对象差异比较工具类
 * 用于比较两个对象相同字段的值，并返回发生变化的字段详情列表
 */
public class BeanDiffUtils {

    /**
     * 比较两个对象的所有声明字段，找出值发生变化的字段
     *
     * @param oldObj 旧对象（变更前的状态）
     * @param newObj 新对象（变更后的状态）
     * @return 包含所有变化字段详情的列表；若任一对象为 null 则返回空列表
     */
    public static List<FieldChangeDetail> diff(Object oldObj, Object newObj) {
        // 初始化结果列表，用于存储字段变化详情
        List<FieldChangeDetail> changes = new ArrayList<>();
        
        // 如果任一对象为 null，无法进行比较，直接返回空列表
        if (oldObj == null || newObj == null) {
            return changes;
        }

        // 获取旧对象类中声明的所有字段（不包括父类字段）
        Field[] fields = oldObj.getClass().getDeclaredFields();
        
        // 遍历每个字段进行逐一比较
        for (Field field : fields) {
            // 设置字段可访问，以便读取私有字段的值
            field.setAccessible(true);
            try {
                // 获取旧对象中该字段的值
                Object v1 = field.get(oldObj);
                // 获取新对象中该字段的值
                Object v2 = field.get(newObj);
                
                // 判断两个字段的值是否不相等
                if (!Objects.equals(v1, v2)) {
                    // 尝试获取字段上的 Schema 注解，用于提取字段的描述信息作为显示标签
                    Schema schema = field.getAnnotation(Schema.class);
                    // 如果存在 Schema 注解且包含描述，则使用描述作为标签；否则使用字段名
                    String label = (schema != null) ? schema.description() : field.getName();
                    
                    // 将字段变化详情（标签、旧值、新值）添加到结果列表中
                    changes.add(new FieldChangeDetail(label, v1, v2));
                }
            } catch (Exception ignored) {
                // 忽略反射操作中可能出现的异常（如非法访问等），继续处理下一个字段
            }
        }
        
        // 返回所有检测到的字段变化详情列表
        return changes;
    }
}