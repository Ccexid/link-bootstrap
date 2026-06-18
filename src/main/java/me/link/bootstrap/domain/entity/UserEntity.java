package me.link.bootstrap.domain.entity;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 用户领域实体，封装登录账号、基础资料、组织归属和租户归属信息。
 */
public class UserEntity {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private Integer userType;

    private String mobile;

    private String email;

    private String avatar;

    private StatusEnum status;

    private Long orgId;

    private Long deptId;

    private String loginIp;

    private LocalDateTime loginDate;

    private Long tenantId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UserEntity(Long id, String username, String password, String nickname, Integer userType, String mobile, String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.userType = userType;
        this.mobile = mobile;
        this.email = email;
        this.avatar = avatar;
        this.status = status;
        this.orgId = orgId;
        this.deptId = deptId;
        this.loginIp = loginIp;
        this.loginDate = loginDate;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserEntity create(String username, String password, String nickname, Integer userType, String mobile, String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        return new UserEntity(null, username, password, nickname, userType, mobile, email, avatar, status, orgId, deptId, loginIp, loginDate, tenantId, null, null);
    }

    public static UserEntity restore(Long id, String username, String password, String nickname, Integer userType, String mobile, String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new UserEntity(id, username, password, nickname, userType, mobile, email, avatar, status, orgId, deptId, loginIp, loginDate, tenantId, createdAt, updatedAt);
    }

    public void changeBasicInfo(String username, String password, String nickname, Integer userType, String mobile, String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.userType = userType;
        this.mobile = mobile;
        this.email = email;
        this.avatar = avatar;
        this.status = status;
        this.orgId = orgId;
        this.deptId = deptId;
        this.loginIp = loginIp;
        this.loginDate = loginDate;
        this.tenantId = tenantId;
    }

    public void enable() {
        this.status = StatusEnum.NORMAL;
    }

    public void disable() {
        this.status = StatusEnum.DISABLE;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getUserType() {
        return userType;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public Long getOrgId() {
        return orgId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public LocalDateTime getLoginDate() {
        return loginDate;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
