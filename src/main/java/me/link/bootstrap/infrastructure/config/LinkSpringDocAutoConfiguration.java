package me.link.bootstrap.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * SpringDoc 接口文档分组配置
 * <p>
 * 按业务模块对API接口进行分组,便于前端查阅和接口管理。
 * 访问地址: <a href="http://localhost:48080/swagger-ui/index.html">...</a>
 * </p>
 *
 * @author Ccexid
 */
@AutoConfiguration
public class LinkSpringDocAutoConfiguration {

    /**
     * 系统管理模块接口分组
     * <p>
     * 包含: 用户管理、角色管理、权限管理、菜单管理等后台管理接口
     * </p>
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("1-系统管理")
                .pathsToMatch(GlobalConstants.API_PREFIX + "/system/**")
                .displayName("系统管理接口")
                .build();
    }

    /**
     * 认证授权模块接口分组
     * <p>
     * 包含: 登录、登出、Token刷新、验证码等认证相关接口
     * </p>
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("2-认证授权")
                .pathsToMatch(GlobalConstants.API_PREFIX + "/auth/**")
                .displayName("认证授权接口")
                .build();
    }

    /**
     * 租户管理模块接口分组
     * <p>
     * 包含: 租户CRUD、租户套餐、租户配置等接口
     * </p>
     */
    @Bean
    public GroupedOpenApi tenantApi() {
        return GroupedOpenApi.builder()
                .group("3-租户管理")
                .pathsToMatch(
                        GlobalConstants.API_PREFIX + "/system/tenants/**",
                        GlobalConstants.API_PREFIX + "/system/tenant-packages/**")
                .displayName("租户管理接口")
                .build();
    }

    /**
     * 全局API文档信息配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Link Platform API 文档")
                        .description("S2P2B2C多租户平台统一接口文档")
                        .version("v0.1.0")
                        .contact(new Contact()
                                .name("Ccexid")
                                .email("863232387@qq.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }


    @Bean
    public ModelResolver snakeCaseModelResolver(ObjectMapper objectMapper){
        return new ModelResolver(objectMapper);
    }
}
