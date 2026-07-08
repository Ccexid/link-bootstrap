package me.link.bootstrap.infrastructure.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfigure {

    /**
     * 创建 SystemClock Bean。
     */
    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
