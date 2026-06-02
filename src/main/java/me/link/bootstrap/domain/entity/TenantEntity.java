package me.link.bootstrap.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 租户领域实体，封装租户基础信息、联系方式、套餐订阅和启停状态。
 */
@Getter
@ToString
@EqualsAndHashCode
public class TenantEntity {

    /**
     * 租户编号 (主键 ID)
     */
    private final Long id;

    /**
     * 租户名
     */
    private final String name;

    /**
     * 联系人的用户编号
     */
    private Long contactUserId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系手机
     */
    private String contactMobile;

    /**
     * 租户状态 (0正常 1停用)
     */
    private StatusEnum status;

    /**
     * 绑定域名数组
     */
    private Set<String> websites;

    /**
     * 租户套餐编号
     */
    private Long packageId;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 账号数量
     */
    private Integer accountCount;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private TenantEntity(Long id,
                         String name,
                         Long contactUserId,
                         String contactName,
                         String contactMobile,
                         StatusEnum status,
                         Set<String> websites,
                         Long packageId,
                         LocalDateTime expireTime,
                         Integer accountCount,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.contactUserId = contactUserId;
        this.contactName = contactName;
        this.contactMobile = contactMobile;
        this.status = status;
        this.websites = copyWebsites(websites);
        this.packageId = packageId;
        this.expireTime = expireTime;
        this.accountCount = accountCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 创建业务对象。
     */
    public static TenantEntity create(String name,
                                      Long contactUserId,
                                      String contactName,
                                      String contactMobile,
                                      Set<String> websites,
                                      Long packageId,
                                      LocalDateTime expireTime,
                                      Integer accountCount) {
        return new TenantEntity(
                null,
                name,
                contactUserId,
                contactName,
                contactMobile,
                StatusEnum.NORMAL,
                websites,
                packageId,
                expireTime,
                accountCount,
                null,
                null
        );
    }

    public static TenantEntity restore(Long id,
                                       String name,
                                       Long contactUserId,
                                       String contactName,
                                       String contactMobile,
                                       StatusEnum status,
                                       Set<String> websites,
                                       Long packageId,
                                       LocalDateTime expireTime,
                                       Integer accountCount,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {
        return new TenantEntity(
                id,
                name,
                contactUserId,
                contactName,
                contactMobile,
                status,
                websites,
                packageId,
                expireTime,
                accountCount,
                createdAt,
                updatedAt
        );
    }

    /**
     * 获取不可变的绑定域名集合
     *
     * @return 不可变的域名集合,防止外部修改内部状态
     */
    public Set<String> getWebsites() {
        if (websites == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(websites);
    }

    public void changeContact(Long contactUserId, String contactName, String contactMobile) {
        this.contactUserId = contactUserId;
        this.contactName = contactName;
        this.contactMobile = contactMobile;
    }

    public void changeWebsites(Set<String> websites) {
        this.websites = copyWebsites(websites);
    }

    public void changePackage(Long packageId, LocalDateTime expireTime, Integer accountCount) {
        this.packageId = packageId;
        this.expireTime = expireTime;
        this.accountCount = accountCount;
    }

    /**
     * 判断租户是否已过期
     *
     * @return true-已过期, false-未过期
     */
    public boolean isExpired() {
        if (expireTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 判断租户是否处于激活状态
     *
     * @return true-激活状态, false-停用状态
     */
    public boolean isActive() {
        return StatusEnum.NORMAL.equals(status);
    }

    /**
     * 停用租户
     */
    public void disable() {
        this.status = StatusEnum.DISABLE;
    }

    /**
     * 启用租户
     */
    public void enable() {
        this.status = StatusEnum.NORMAL;
    }

    private static Set<String> copyWebsites(Set<String> websites) {
        if (websites == null) {
            return null;
        }
        return new HashSet<>(websites);
    }
}
