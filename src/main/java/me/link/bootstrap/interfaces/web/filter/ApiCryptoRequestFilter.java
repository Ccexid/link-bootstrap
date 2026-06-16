package me.link.bootstrap.interfaces.web.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import me.link.bootstrap.infrastructure.crypto.ApiCryptoService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 将加密请求体解密为原始 JSON 后再交给 Spring MVC 绑定。
 */
public class ApiCryptoRequestFilter extends OncePerRequestFilter {

    private static final Set<String> BODY_METHODS = Set.of(
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name()
    );

    private final ApiCryptoService apiCryptoService;
    private final ObjectMapper objectMapper;

    public ApiCryptoRequestFilter(ApiCryptoService apiCryptoService, ObjectMapper objectMapper) {
        this.apiCryptoService = apiCryptoService;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!shouldDecrypt(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String encryptedParameter = request.getParameter(apiCryptoService.properties().getRequestField());
        if (StringUtils.hasText(encryptedParameter) && !isJsonRequest(request)) {
            String plainParameters = apiCryptoService.decryptRequest(encryptedParameter);
            filterChain.doFilter(new DecryptedParameterRequest(request, toParameterMap(plainParameters)), response);
            return;
        }

        String requestBody = StreamUtils.copyToString(request.getInputStream(), resolveCharset(request));
        if (!StringUtils.hasText(requestBody)) {
            filterChain.doFilter(request, response);
            return;
        }

        JsonNode root = objectMapper.readTree(requestBody);
        JsonNode encryptedNode = root.get(apiCryptoService.properties().getRequestField());
        if (encryptedNode == null || !encryptedNode.isTextual()) {
            throw new IllegalArgumentException("请求体必须包含密文字段: " + apiCryptoService.properties().getRequestField());
        }

        String plainBody = apiCryptoService.decryptRequest(encryptedNode.asText());
        filterChain.doFilter(new DecryptedBodyRequest(request, plainBody), response);
    }

    private boolean shouldDecrypt(HttpServletRequest request) {
        return apiCryptoService.properties().matches(request.getRequestURI())
                && ((BODY_METHODS.contains(request.getMethod()) && isJsonRequest(request))
                || StringUtils.hasText(request.getParameter(apiCryptoService.properties().getRequestField())));
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && MediaType.parseMediaType(contentType).isCompatibleWith(MediaType.APPLICATION_JSON);
    }

    private Charset resolveCharset(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        return StringUtils.hasText(encoding) ? Charset.forName(encoding) : StandardCharsets.UTF_8;
    }

    private Map<String, String[]> toParameterMap(String plainParameters) throws IOException {
        JsonNode root = objectMapper.readTree(plainParameters);
        if (!root.isObject()) {
            throw new IllegalArgumentException("请求参数密文解密后必须是 JSON 对象");
        }

        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        root.fields().forEachRemaining(entry -> {
            JsonNode value = entry.getValue();
            if (value == null || value.isNull()) {
                return;
            }
            if (value.isArray()) {
                String[] values = new String[value.size()];
                for (int i = 0; i < value.size(); i++) {
                    values[i] = stringifyParameterValue(value.get(i));
                }
                parameterMap.put(entry.getKey(), values);
                return;
            }
            parameterMap.put(entry.getKey(), new String[]{stringifyParameterValue(value)});
        });
        return parameterMap;
    }

    private String stringifyParameterValue(JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isValueNode()) {
            return value.asText();
        }
        return value.toString();
    }

    private static final class DecryptedBodyRequest extends HttpServletRequestWrapper {

        private final byte[] body;

        private DecryptedBodyRequest(HttpServletRequest request, String body) {
            super(request);
            this.body = body.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    // Synchronous request body wrapper.
                }

                @Override
                public int read() {
                    return inputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }

        @Override
        public int getContentLength() {
            return body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body.length;
        }
    }

    private final class DecryptedParameterRequest extends HttpServletRequestWrapper {

        private final Map<String, String[]> parameterMap;

        private DecryptedParameterRequest(HttpServletRequest request, Map<String, String[]> decryptedParameters) {
            super(request);
            Map<String, String[]> merged = new LinkedHashMap<>(request.getParameterMap());
            merged.remove(apiCryptoService.properties().getRequestField());
            merged.putAll(decryptedParameters);
            this.parameterMap = Collections.unmodifiableMap(merged);
        }

        @Override
        public String getParameter(String name) {
            String[] values = getParameterValues(name);
            return values == null || values.length == 0 ? null : values[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return parameterMap;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(parameterMap.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            return parameterMap.get(name);
        }
    }
}
