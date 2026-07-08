package me.link.bootstrap.interfaces.web.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.link.bootstrap.infrastructure.crypto.ApiCryptoProperties;
import me.link.bootstrap.infrastructure.crypto.ApiCryptoService;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiCryptoResponseBodyAdviceTest {

    private ApiCryptoService apiCryptoService;
    private ApiCryptoResponseBodyAdvice advice;
    private MethodParameter returnType;

    /**
     * 准备测试上下文。
     */
    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        ApiCryptoProperties properties = new ApiCryptoProperties();
        properties.setEnabled(true);
        properties.setPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        properties.setPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));

        apiCryptoService = new ApiCryptoService(properties);
        advice = new ApiCryptoResponseBodyAdvice(apiCryptoService, new ObjectMapper());
        returnType = MethodParameter.forExecutable(
                TestController.class.getDeclaredMethod("sample"),
                -1
        );
    }

    /**
     * 验证 shouldEncryptMatchedApiResponse 场景。
     */
    @Test
    void shouldEncryptMatchedApiResponse() {
        ResultResponse<String> body = ResultResponse.success("ok");

        Object encrypted = advice.beforeBodyWrite(
                body,
                returnType,
                MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class,
                serverRequest("/api/v1/system/users"),
                new ServletServerHttpResponse(new MockHttpServletResponse())
        );

        assertThat(encrypted).isInstanceOf(Map.class);
        Object encryptedText = ((Map<?, ?>) encrypted).get("data");
        assertThat(encryptedText).isInstanceOf(String.class);
        assertThat(apiCryptoService.decryptRequest((String) encryptedText)).contains("\"data\":\"ok\"");
    }

    /**
     * 验证 shouldNotEncryptExcludedPublicKeyResponse 场景。
     */
    @Test
    void shouldNotEncryptExcludedPublicKeyResponse() {
        ResultResponse<String> body = ResultResponse.success("public-key");

        Object result = advice.beforeBodyWrite(
                body,
                returnType,
                MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class,
                serverRequest("/api/v1/auth/public-keys/current"),
                new ServletServerHttpResponse(new MockHttpServletResponse())
        );

        assertThat(result).isSameAs(body);
    }

    /**
     * 验证 serverRequest 场景。
     */
    private ServletServerHttpRequest serverRequest(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.setRequestURI(path);
        return new ServletServerHttpRequest(request);
    }

    private static final class TestController {

        /**
         * 返回测试样例数据。
         */
        @GetMapping
        ResultResponse<String> sample() {
            return ResultResponse.success("ok");
        }
    }
}
