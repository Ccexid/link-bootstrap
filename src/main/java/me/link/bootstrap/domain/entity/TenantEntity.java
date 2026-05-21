package me.link.bootstrap.domain.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
public class TenantEntity {

    /**
     * 租户编号 (主键 ID)
     */
    private Long id;

    /**
     * 租户名
     */
    private String name;

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

    /**
     * 设置绑定域名集合(内部会创建副本)
     *
     * @param websites 域名集合
     */
    public void setWebsites(Set<String> websites) {
        if (websites == null) {
            this.websites = null;
        } else {
            this.websites = new HashSet<>(websites);
        }
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
}
