package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.TenantEntity;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 租户实体工厂
 * <p>
 * 封装TenantEntity的创建逻辑,确保创建时满足业务不变量:
 * - 租户名不能为空
 * - 联系人姓名不能为空
 * - 过期时间必须晚于当前时间
 * - 账号数量必须大于0
 * - 初始状态为正常
 * </p>
 *
 * @author Ccexid
 */
public final class TenantFactory {

    private static final int CONTACT_MOBILE_MAX_LENGTH = 20;
    private static final int WEBSITE_MAX_COUNT = 20;
    private static final int WEBSITE_MAX_LENGTH = 253;
    private static final Pattern CONTACT_MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern WEBSITE_PATTERN = Pattern.compile("^(?=.{1,253}$)(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,63}$");

    private TenantFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    /**
     * 创建租户实体(完整参数)
     *
     * @param name          租户名(必填)
     * @param contactUserId 联系人用户编号(可选)
     * @param contactName   联系人姓名(必填)
     * @param contactMobile 联系手机(可选)
     * @param websites      绑定域名集合(可选)
     * @param packageId     租户套餐编号(必填)
     * @param expireTime    过期时间(必填,必须晚于当前时间)
     * @param accountCount  账号数量(必填,必须大于0)
     * @return 创建好的TenantEntity实例
     * @throws IllegalArgumentException 当必填项为空或业务规则不满足时抛出
     */
    public static TenantEntity create(String name,
                                      Long contactUserId,
                                      String contactName,
                                      String contactMobile,
                                      Set<String> websites,
                                      Long packageId,
                                      LocalDateTime expireTime,
                                      Integer accountCount) {
        validateRequiredFields(name, contactName, packageId, expireTime, accountCount);
        validateBusinessRules(expireTime, accountCount);

        String normalizedContactMobile = normalizeContactMobile(contactMobile);
        Set<String> normalizedWebsites = normalizeWebsites(websites);

        return TenantEntity.create(
                name.trim(),
                contactUserId,
                contactName.trim(),
                normalizedContactMobile,
                normalizedWebsites,
                packageId,
                expireTime,
                accountCount
        );
    }

    /**
     * 创建租户实体(简化参数,仅必填项)
     *
     * @param name         租户名
     * @param contactName  联系人姓名
     * @param packageId    租户套餐编号
     * @param expireTime   过期时间
     * @param accountCount 账号数量
     * @return 创建好的TenantEntity实例
     * @throws IllegalArgumentException 当必填项为空或业务规则不满足时抛出
     */
    public static TenantEntity createSimple(String name,
                                            String contactName,
                                            Long packageId,
                                            LocalDateTime expireTime,
                                            Integer accountCount) {
        return create(name, null, contactName, null, null, packageId, expireTime, accountCount);
    }

    /**
     * 创建租户实体(Builder模式,适合可选参数较多的场景)
     *
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 校验必填字段
     *
     * @param name         租户名
     * @param contactName  联系人姓名
     * @param packageId    套餐编号
     * @param expireTime   过期时间
     * @param accountCount 账号数量
     */
    private static void validateRequiredFields(String name,
                                               String contactName,
                                               Long packageId,
                                               LocalDateTime expireTime,
                                               Integer accountCount) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("租户名不能为空");
        }
        if (contactName == null || contactName.trim().isEmpty()) {
            throw new IllegalArgumentException("联系人姓名不能为空");
        }
        if (packageId == null) {
            throw new IllegalArgumentException("租户套餐编号不能为空");
        }
        if (expireTime == null) {
            throw new IllegalArgumentException("过期时间不能为空");
        }
        if (accountCount == null) {
            throw new IllegalArgumentException("账号数量不能为空");
        }
    }

    /**
     * 校验业务规则
     *
     * @param expireTime   过期时间
     * @param accountCount 账号数量
     */
    private static void validateBusinessRules(LocalDateTime expireTime, Integer accountCount) {
        if (!expireTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("过期时间必须晚于当前时间");
        }
        if (accountCount <= 0) {
            throw new IllegalArgumentException("账号数量必须大于0");
        }
    }

    private static String normalizeContactMobile(String contactMobile) {
        if (contactMobile == null || contactMobile.trim().isEmpty()) {
            return null;
        }
        String normalizedContactMobile = contactMobile.trim();
        if (normalizedContactMobile.length() > CONTACT_MOBILE_MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("联系手机长度不能超过%d个字符", CONTACT_MOBILE_MAX_LENGTH));
        }
        if (!CONTACT_MOBILE_PATTERN.matcher(normalizedContactMobile).matches()) {
            throw new IllegalArgumentException("联系手机格式不正确");
        }
        return normalizedContactMobile;
    }

    private static Set<String> normalizeWebsites(Set<String> websites) {
        if (websites == null || websites.isEmpty()) {
            return null;
        }
        if (websites.size() > WEBSITE_MAX_COUNT) {
            throw new IllegalArgumentException(String.format("绑定域名数量不能超过%d个", WEBSITE_MAX_COUNT));
        }
        Set<String> normalizedWebsites = new LinkedHashSet<>();
        for (String website : websites) {
            if (website == null || website.trim().isEmpty()) {
                throw new IllegalArgumentException("绑定域名不能为空");
            }
            String normalizedWebsite = website.trim().toLowerCase();
            if (normalizedWebsite.length() > WEBSITE_MAX_LENGTH) {
                throw new IllegalArgumentException(String.format("绑定域名长度不能超过%d个字符", WEBSITE_MAX_LENGTH));
            }
            if (!WEBSITE_PATTERN.matcher(normalizedWebsite).matches()) {
                throw new IllegalArgumentException(String.format("绑定域名格式不正确: %s", normalizedWebsite));
            }
            normalizedWebsites.add(normalizedWebsite);
        }
        return normalizedWebsites;
    }

    /**
     * 租户实体构建器
     * <p>
     * 提供流式API创建TenantEntity,适合可选参数较多的场景。
     * 调用build()时会自动执行所有校验。
     * </p>
     */
    public static class Builder {
        private String name;
        private Long contactUserId;
        private String contactName;
        private String contactMobile;
        private Set<String> websites;
        private Long packageId;
        private LocalDateTime expireTime;
        private Integer accountCount;

        /**
         * 设置租户名(必填)
         *
         * @param name 租户名
         * @return Builder实例
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置联系人用户编号(可选)
         *
         * @param contactUserId 联系人用户编号
         * @return Builder实例
         */
        public Builder contactUserId(Long contactUserId) {
            this.contactUserId = contactUserId;
            return this;
        }

        /**
         * 设置联系人姓名(必填)
         *
         * @param contactName 联系人姓名
         * @return Builder实例
         */
        public Builder contactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        /**
         * 设置联系手机(可选)
         *
         * @param contactMobile 联系手机
         * @return Builder实例
         */
        public Builder contactMobile(String contactMobile) {
            this.contactMobile = contactMobile;
            return this;
        }

        /**
         * 设置绑定域名集合(可选)
         *
         * @param websites 域名集合
         * @return Builder实例
         */
        public Builder websites(Set<String> websites) {
            this.websites = websites;
            return this;
        }

        /**
         * 设置租户套餐编号(必填)
         *
         * @param packageId 套餐编号
         * @return Builder实例
         */
        public Builder packageId(Long packageId) {
            this.packageId = packageId;
            return this;
        }

        /**
         * 设置过期时间(必填)
         *
         * @param expireTime 过期时间
         * @return Builder实例
         */
        public Builder expireTime(LocalDateTime expireTime) {
            this.expireTime = expireTime;
            return this;
        }

        /**
         * 设置账号数量(必填)
         *
         * @param accountCount 账号数量
         * @return Builder实例
         */
        public Builder accountCount(Integer accountCount) {
            this.accountCount = accountCount;
            return this;
        }

        /**
         * 构建TenantEntity实例
         * <p>
         * 执行所有必填字段校验和业务规则校验,通过后返回TenantEntity实例。
         * </p>
         *
         * @return 创建好的TenantEntity实例
         * @throws IllegalArgumentException 当必填项为空或业务规则不满足时抛出
         */
        public TenantEntity build() {
            return TenantFactory.create(
                    name,
                    contactUserId,
                    contactName,
                    contactMobile,
                    websites,
                    packageId,
                    expireTime,
                    accountCount
            );
        }
    }
}
