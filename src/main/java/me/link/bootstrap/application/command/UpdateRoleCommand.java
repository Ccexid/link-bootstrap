package me.link.bootstrap.application.command;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public record UpdateRoleCommand(Long id, String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark, Long tenantId) {
}
