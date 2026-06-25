package me.link.bootstrap.infrastructure.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.config.LinkSecurityProperties;
import me.link.bootstrap.shared.kernel.config.ClientIpProperties;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * 登录人机校验服务。
 * <p>
 * 默认关闭。开启后在登录业务执行前调用第三方服务端校验接口,适配 reCAPTCHA / Turnstile
 * 等返回 {@code {"success": true}} 结构的校验服务。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HumanVerificationService {

    private final LinkSecurityProperties securityProperties;
    private final ClientIpProperties clientIpProperties;
    private final ObjectMapper objectMapper;

    /**
     * 校验人机验证 token。未启用时直接放行。
     */
    public void verify(String captchaToken) {
        LinkSecurityProperties.HumanVerification properties = securityProperties.getHumanVerification();
        if (!properties.isEnabled()) {
            return;
        }
        validateProperties(properties);

        String token = StringUtils.trimToEmpty(captchaToken);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ErrorCode.HUMAN_VERIFICATION_FAILED, "请先完成人机校验");
        }

        boolean passed = remoteVerify(token, properties);
        if (!passed) {
            throw new BusinessException(ErrorCode.HUMAN_VERIFICATION_FAILED);
        }
    }

    private boolean remoteVerify(String captchaToken, LinkSecurityProperties.HumanVerification properties) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(properties.getSecretParam(), properties.getSecret());
        form.add(properties.getResponseParam(), captchaToken);

        String clientIp = clientIp();
        if (StringUtils.isNotBlank(properties.getRemoteIpParam()) && StringUtils.isNotBlank(clientIp)) {
            form.add(properties.getRemoteIpParam(), clientIp);
        }

        try {
            String body = RestClient.builder()
                    .requestFactory(clientHttpRequestFactory(properties.getTimeout()))
                    .build()
                    .post()
                    .uri(properties.getVerifyUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(String.class);
            JsonNode root = objectMapper.readTree(body);
            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                log.warn("人机校验未通过: errorCodes={}", root.path("error-codes"));
            }
            return success;
        } catch (RestClientException ex) {
            log.warn("人机校验服务调用失败", ex);
            throw new BusinessException(ErrorCode.HUMAN_VERIFICATION_FAILED, "人机校验服务不可用");
        } catch (Exception ex) {
            log.warn("人机校验响应解析失败", ex);
            throw new BusinessException(ErrorCode.HUMAN_VERIFICATION_FAILED, "人机校验响应异常");
        }
    }

    private org.springframework.http.client.SimpleClientHttpRequestFactory clientHttpRequestFactory(Duration timeout) {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return factory;
    }

    private void validateProperties(LinkSecurityProperties.HumanVerification properties) {
        if (StringUtils.isBlank(properties.getVerifyUrl())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "人机校验地址未配置");
        }
        if (StringUtils.isBlank(properties.getSecret())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "人机校验密钥未配置");
        }
        if (StringUtils.isBlank(properties.getResponseParam())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "人机校验 token 参数名未配置");
        }
        if (StringUtils.isBlank(properties.getSecretParam())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "人机校验密钥参数名未配置");
        }
        if (properties.getTimeout() == null || properties.getTimeout().isNegative() || properties.getTimeout().isZero()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "人机校验超时时间必须大于 0");
        }
    }

    private String clientIp() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return "";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = null;
        if (clientIpProperties.isTrustForwardHeaders()) {
            for (String headerName : clientIpProperties.getForwardHeaders()) {
                String headerValue = request.getHeader(headerName);
                if (StringUtils.isNotBlank(headerValue)) {
                    ip = pickRightmostIp(headerValue);
                    break;
                }
            }
        }
        return StringUtils.defaultIfBlank(ip, request.getRemoteAddr());
    }

    private String pickRightmostIp(String headerValue) {
        int lastComma = headerValue.lastIndexOf(',');
        String candidate = lastComma >= 0 ? headerValue.substring(lastComma + 1) : headerValue;
        return candidate.trim();
    }
}
