package me.link.bootstrap.infrastructure.config;

import lombok.Data;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 安全相关配置项,绑定 {@code link.security.*} 配置。
 * <p>配置示例:</p>
 * <pre>
 * link:
 *   security:
 *     login:
 *       max-fail-attempts: 5
 *       lock-duration: 15m
 * </pre>
 */
@Data
@Component
@ConfigurationProperties(prefix = "link.security")
public class LinkSecurityProperties {

    /** 登录策略配置。 */
    private Login login = new Login();

    /** 邮箱验证码配置。 */
    private EmailCode emailCode = new EmailCode();

    /** 登录人机校验配置。 */
    private HumanVerification humanVerification = new HumanVerification();

    /** 跨域资源共享配置。 */
    private Cors cors = new Cors();

    @Data
    public static class Login {
        /** 触发账号锁定的连续失败次数阈值。 */
        private int maxFailAttempts = 5;

        /** 账号锁定持续时长,默认 15 分钟。 */
        private Duration lockDuration = Duration.ofMinutes(15);
    }

    @Data
    public static class EmailCode {
        /** 验证码有效期。 */
        private Duration ttl = Duration.ofMinutes(5);

        /** 数字验证码长度。 */
        private int length = 6;

        /** 同一验证码允许的最大错误校验次数。 */
        private int maxVerifyAttempts = 5;

        /** Redis 摘要签名密钥,生产环境必须通过环境变量或配置中心覆盖。 */
        private String secret = "link-bootstrap-dev-email-code-secret";

        /** 发件人地址。为空时使用 spring.mail.username。 */
        private String from;

        /** 发件人展示名,预留给后续 MIME 邮件使用。 */
        private String senderName = "Link Platform";

        /** 邮件模板中展示的系统名称。 */
        private String systemName = "Link Platform";

        /** 邮件模板中展示的公司名称。 */
        private String companyName = "Link Platform";

        /** 邮件标题。 */
        private String subject = "登录验证码";

        /** 同一 IP + 邮箱组合发送验证码的限流窗口。 */
        private Duration sendIpEmailWindow = Duration.ofMinutes(10);

        /** 同一 IP + 邮箱组合在限流窗口内允许发送验证码的最大次数。 */
        private long sendIpEmailMaxRequests = 3;
    }

    @Data
    public static class HumanVerification {
        /** 是否启用登录人机校验。 */
        private boolean enabled = false;

        /** 人机校验服务端验证地址。 */
        private String verifyUrl;

        /** 人机校验服务端密钥。 */
        private String secret;

        /** 请求 token 参数名。 */
        private String responseParam = "response";

        /** 请求密钥参数名。 */
        private String secretParam = "secret";

        /** 请求客户端 IP 参数名。为空则不传。 */
        private String remoteIpParam = "remoteip";

        /** 远程校验超时时间。 */
        private Duration timeout = Duration.ofSeconds(3);
    }

    @Data
    public static class Cors {
        /** 允许跨域访问的来源模式。生产环境必须配置为明确域名白名单。 */
        private List<String> allowedOriginPatterns = new ArrayList<>(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));

        /** 允许的 HTTP 方法。 */
        private List<String> allowedMethods = new ArrayList<>(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        /** 允许的请求头。 */
        private List<String> allowedHeaders = new ArrayList<>(List.of("*"));

        /** 暴露给前端读取的响应头。 */
        private List<String> exposedHeaders = new ArrayList<>(List.of(GlobalConstants.TRACE_ID_HEADER));

        /** 是否允许 Cookie、Authorization 等凭证跨域传输。 */
        private boolean allowCredentials = true;

        /** 预检请求缓存时间。 */
        private Duration maxAge = Duration.ofHours(1);
    }
}
