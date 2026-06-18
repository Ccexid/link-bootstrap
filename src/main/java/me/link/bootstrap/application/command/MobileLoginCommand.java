package me.link.bootstrap.application.command;

/**
 * 手机验证码登录命令。
 */
public record MobileLoginCommand(String mobile, String code) {
}
