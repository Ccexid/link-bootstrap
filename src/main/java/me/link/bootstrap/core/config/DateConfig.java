package me.link.bootstrap.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class DateConfig {

    @Bean
    public Clock clock() {
        // 统一使用系统默认时区（或强制指定 "Asia/Shanghai"）
        // 这样即便服务器物理时区不同，业务时间也是统一的
        return Clock.system(ZoneId.of("Asia/Shanghai"));
    }
}