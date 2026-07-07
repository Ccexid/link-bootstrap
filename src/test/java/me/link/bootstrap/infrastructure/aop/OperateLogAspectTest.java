package me.link.bootstrap.infrastructure.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.link.bootstrap.application.service.OperateLogService;
import me.link.bootstrap.shared.kernel.config.ClientIpProperties;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class OperateLogAspectTest {

    private final OperateLogAspect operateLogAspect = new OperateLogAspect(
            mock(OperateLogService.class),
            new ObjectMapper(),
            new ClientIpProperties()
    );

    @Test
    @SuppressWarnings("unchecked")
    void shouldMaskVerificationCodeFieldsRecursively() {
        Map<String, Object> source = Map.of(
                "email", "admin@example.com",
                "code", "123456",
                "captcha_token", "captcha-plain",
                "profile", Map.of(
                        "verifyCode", "654321",
                        "emailCode", "111222",
                        "nickname", "admin"
                ),
                "items", List.of(
                        Map.of(
                                "sms_code", "333444",
                                "name", "first"
                        )
                )
        );

        Map<String, Object> sanitized = (Map<String, Object>) ReflectionTestUtils.invokeMethod(
                operateLogAspect,
                "sanitizeMap",
                source
        );

        assertThat(sanitized)
                .containsEntry("email", "admin@example.com")
                .containsEntry("code", "******")
                .containsEntry("captcha_token", "******");
        assertThat((Map<String, Object>) sanitized.get("profile"))
                .containsEntry("verifyCode", "******")
                .containsEntry("emailCode", "******")
                .containsEntry("nickname", "admin");
        List<Map<String, Object>> items = (List<Map<String, Object>>) sanitized.get("items");
        assertThat(items.get(0))
                .containsEntry("sms_code", "******")
                .containsEntry("name", "first");
    }
}
