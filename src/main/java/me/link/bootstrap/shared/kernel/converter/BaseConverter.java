package me.link.bootstrap.shared.kernel.converter;

import org.mapstruct.*;

/**
 * MapStruct 全局通用转换配置基接口
 * <p>
 * 定义了统一的转换策略与通用的泛型基础转换方法，子类 Mapper 只需继承此接口即可。
 * </p>
 *
 * <p><strong>重要说明：</strong></p>
 * <ul>
 *     <li>子类继承时必须明确指定泛型类型，例如：{@code @Mapper(config = BaseConverter.class) public interface ResponseVOConverter}</li>
 *     <li>如果正向转换（convert）定义了字段映射，逆向转换（reverseConvert）会自动继承反向映射规则</li>
 *     <li>当源对象为 null 时，转换方法返回 null；当源集合为 null 时，返回空列表而非 null</li>
 * </ul>
 *
 * @author Ccexid
 */
@MapperConfig(
        // 1. 自动将生成的实现类注册为 Spring 的 Bean，支持通过 @Autowired 注入
        componentModel = MappingConstants.ComponentModel.SPRING,

        // 2. 生产环境避坑关键：未映射的目标字段（Target）抛出 ERROR。
        // 原因：业务扩展增删字段时，开发人员极其容易漏掉转换逻辑。设为 ERROR 可以在编译期直接拦截，保障核心数据不丢失。
        unmappedTargetPolicy = ReportingPolicy.ERROR,

        // 3. 源字段（Source）多出来的字段通常是由于 DTO 或 Entity 字段不一致，可以安全忽略
        unmappedSourcePolicy = ReportingPolicy.IGNORE,

        // 4. 当集合或列表为 null 时，自动返回空列表（如 Collections.emptyList()）而不是返回 null，规避下游 NPE 崩溃
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,

        // 5. 显式指定依赖注入策略为构造函数注入，有利于单元测试和保证不可变性
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,

        // 6. 对 null 值的处理策略：在转换前检查源对象是否为 null
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,

        // 7. 当源属性为 null 时，目标属性保持默认值（不设置 null）
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BaseConverter {
}
