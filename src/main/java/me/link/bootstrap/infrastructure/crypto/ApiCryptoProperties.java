package me.link.bootstrap.infrastructure.crypto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

import static me.link.bootstrap.shared.kernel.constant.GlobalConstants.API_PREFIX;

/**
 * API RSA 加解密配置,绑定 {@code link.api-crypto.*}。
 */
@Data
@ConfigurationProperties(prefix = "link.api-crypto")
public class ApiCryptoProperties {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** 是否启用接口加解密。 */
    private boolean enabled = false;

    /** 请求体密文字段名。 */
    private String requestField = "data";

    /** 响应体密文字段名。 */
    private String responseField = "data";

    /** 客户端使用服务端公钥加密请求,服务端用该私钥解密。 */
    private String privateKey;

    /** 服务端使用客户端公钥加密响应,客户端用对应私钥解密。 */
    private String publicKey;

    /** 需要加解密的路径。 */
    private List<String> includePaths = new ArrayList<>(List.of(API_PREFIX + "/**"));

    /** 无需加解密的路径。 */
    private List<String> excludePaths = new ArrayList<>(List.of(
            API_PREFIX + "/auth/public-keys/current",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    ));

    public boolean matches(String path) {
        if (!enabled) {
            return false;
        }
        boolean included = includePaths.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
        if (!included) {
            return false;
        }
        return excludePaths.stream().noneMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }
}
