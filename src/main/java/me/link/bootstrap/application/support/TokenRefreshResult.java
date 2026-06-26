package me.link.bootstrap.application.support;

/**
 * Token 查询或刷新结果，承载 Sa-Token 当前签发信息和剩余有效期。
 */
public record TokenRefreshResult(
        String tokenName,
        String tokenValue,
        String tokenPrefix,
        long tokenTimeout,
        long tokenActiveTimeout
) {
}
