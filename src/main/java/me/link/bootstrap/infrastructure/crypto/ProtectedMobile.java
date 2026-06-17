package me.link.bootstrap.infrastructure.crypto;

/**
 * 手机号落库保护后的三元组。
 *
 * @param cipher     可逆密文
 * @param hash       HMAC 检索哈希
 * @param mask       脱敏展示值
 * @param keyVersion 生成密文时使用的密钥版本
 */
public record ProtectedMobile(String cipher, String hash, String mask, Integer keyVersion) {
}
