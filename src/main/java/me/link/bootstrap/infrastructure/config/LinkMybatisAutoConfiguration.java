package me.link.bootstrap.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.parser.cache.JdkSerialCaffeineJsqlParseCache;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.persistence.handler.LinkDefaultDBFieldHandler;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * MyBatis-Plus 自动配置类
 *
 * @author Ccexid
 */
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
@MapperScan(
        basePackages = "me.link.bootstrap.infrastructure.persistence.repository",
        annotationClass = Mapper.class,
        lazyInitialization = "${mybatis.lazy-initialization:false}"
)
@Slf4j
public class LinkMybatisAutoConfiguration {

    static {
        // 1. 优化 JsqlParser 语法解析器缓存：
        // 提升高并发下 MyBatis-Plus 动态解析 SQL（如分页、租户过滤）的吞吐量，规避频繁的内存垃圾回收 (GC)
        JsqlParserGlobal.setJsqlParseCache(new JdkSerialCaffeineJsqlParseCache(cache -> cache
                .maximumSize(1024)
                .expireAfterWrite(5, TimeUnit.SECONDS))
        );
    }

    /**
     * 配置 MyBatis-Plus 核心插件链
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("[MyBatis-Plus] 配置 MyBatis-Plus 插件链...");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 2. 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 3. 分页插件：显式指定 DbType.MYSQL 规避自动探测带来的多数据源适配风险及启动耗时
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);

        // 如果单页查询量过大，建议在此处限制单页最大条数，防止假死（如：限制单页最多 500 条）
        paginationInterceptor.setMaxLimit(500L);

        interceptor.addInnerInterceptor(paginationInterceptor);

        return interceptor;
    }

    @Bean
    public LinkDefaultDBFieldHandler defaultDBFieldHandler() {
        return new LinkDefaultDBFieldHandler();
    }
}
