package me.link.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动入口，负责引导 Spring Boot 容器并加载项目自动配置。
 */
@SpringBootApplication
public class LinkMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkMainApplication.class, args);
    }

}
