package me.link.bootstrap.infrastructure.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import me.link.bootstrap.shared.kernel.jackson.deserializer.XssStringDeserializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigInteger;

/**
 * Jackson 全局自动配置中心
 * <p>
 * 使用 @AutoConfiguration 声明为高等级的自动配置类，
 * 并配置在 JacksonAutoConfiguration 之前执行，确保我们的定制可以无缝融入 Spring 体系。
 * </p>
 *
 * @author 7Link
 */
@AutoConfiguration(before = JacksonAutoConfiguration.class)
@ConditionalOnClass(Jackson2ObjectMapperBuilderCustomizer.class)
public class LinkJacksonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomizer() {
        return builder -> {
            // 1. 基础特性配置
            // 开启以枚举的 toString() 名来序列化输出
            builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

            // 💡 核心改良：不要手动 new JavaTimeModule()，因为那会覆盖掉我们在 application.yml 里配置的
            // spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
            // Spring 会自动帮我们找到并注册 JavaTimeModule，无需在此处手动干预导致配置失效。

            // 2. 解决大数值（Long/BigInteger）传给前端 JavaScript 时的精度截断问题
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            builder.serializerByType(BigInteger.class, ToStringSerializer.instance);

            // 3. 全局 XSS 过滤注入
            builder.deserializerByType(String.class, new XssStringDeserializer());
        };
    }
}
