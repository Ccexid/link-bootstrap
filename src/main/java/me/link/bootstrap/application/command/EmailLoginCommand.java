package me.link.bootstrap.application.command;

/**
 * 邮箱验证码登录命令。
 */
public record EmailLoginCommand(String email, String code, String captchaToken) {
}
