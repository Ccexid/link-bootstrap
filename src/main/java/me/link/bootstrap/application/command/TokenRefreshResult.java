package me.link.bootstrap.application.command;

public record TokenRefreshResult(
        String tokenName,
        String tokenValue,
        String tokenPrefix,
        long tokenTimeout,
        long tokenActiveTimeout
) {
}
