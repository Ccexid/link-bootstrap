package me.link.bootstrap.shared.kernel.jackson.deserializer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 安全的 XSS 字符串反序列化清洗器
 */
public class XssStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (StrUtil.isBlank(value)) {
            return value;
        }

        // 💡 生产环境避坑提示：
        // 如果该字段是密码、或者本身就是富文本内容，一刀切过滤会引发业务 Bug。
        // 推荐：仅清除可能带 script 的危险标签，或者配合业务在 Controller 层做精细化过滤。
        // 这里采用 Hutool 相对温和的转义，或者直接过滤（根据项目严格度决定）
        return HtmlUtil.filter(value);
    }
}
