package me.link.bootstrap.infrastructure.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局自动配置中心。
 * <p>
 * 与 {@code application.yml} 中 {@code spring.jackson.*} 配置协同生效,集中处理以下约定:
 * </p>
 * <ol>
 *   <li>枚举序列化使用 {@code toString()}(yml 命名策略 + 注解优先级二选一);</li>
 *   <li>{@code Long / BigInteger} 序列化为字符串,规避 JS 端 53 位整数精度丢失;</li>
 *   <li>{@link LocalDateTime} 统一使用 {@code yyyy-MM-dd HH:mm:ss} 字符串格式 — yml 的
 *       {@code date-format} 仅对 {@code java.util.Date} 直接生效,{@code java.time} 类型
 *       须经 JSR-310 模块的 serializer/deserializer 显式声明,故在此显式注册。</li>
 * </ol>
 *
 * @author 7Link
 */
@AutoConfiguration(before = JacksonAutoConfiguration.class)
@ConditionalOnClass(Jackson2ObjectMapperBuilderCustomizer.class)
public class LinkJacksonAutoConfiguration {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @SuppressWarnings("null")
    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomizer() {
        return builder -> {
            // 1. 枚举以 toString() 输出(对 @JsonValue 标注的枚举无效,@JsonValue 优先级更高)
            builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

            // 2. 大整型转字符串,防 JavaScript 精度截断(Number.MAX_SAFE_INTEGER = 2^53 - 1)
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            builder.serializerByType(BigInteger.class, ToStringSerializer.instance);

            // 3. LocalDateTime 全局统一为 yyyy-MM-dd HH:mm:ss 字符串(覆盖 yml 的时间戳输出,
            //    并与 application.yml 中 time-zone: GMT+8 协同;字段级 @JsonFormat 仍可覆盖此默认)
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        };
    }
}
