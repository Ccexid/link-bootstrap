CREATE TABLE `system_sequence`
(
    `id`            bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `biz_code`      varchar(64) NOT NULL COMMENT '业务标识(如: ORDER, TENANT)',
    `current_value` bigint      NOT NULL DEFAULT '0' COMMENT '当前最大序列号',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_biz_code` (`biz_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分布式序列号维护表';

CREATE TABLE `system_tenant_package`
(
    `id`          bigint UNSIGNED NOT NULL COMMENT '套餐编号',
    `name`        varchar(30) NOT NULL COMMENT '套餐名',
    `status`      tinyint     NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
    `remark`      varchar(256)         DEFAULT '' COMMENT '备注',
    `menu_ids`    json        NOT NULL COMMENT '关联的菜单编号数组',
    `creator`     varchar(32) NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(32)          DEFAULT '' COMMENT '更新者',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     tinyint (1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删 1已删)',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租户套餐表';

CREATE TABLE `system_tenant`
(
    `id`              bigint UNSIGNED NOT NULL COMMENT '租户编号',
    `name`            varchar(64) NOT NULL COMMENT '租户名',
    `contact_user_id` bigint               DEFAULT NULL COMMENT '联系人的用户编号',
    `contact_name`    varchar(32) NOT NULL COMMENT '联系人',
    `contact_mobile`  varchar(128)         DEFAULT NULL COMMENT '联系手机(加密存储)',
    `status`          tinyint     NOT NULL DEFAULT 0 COMMENT '状态',
    `websites`        json                 DEFAULT NULL COMMENT '绑定域名数组',
    `package_id`      bigint      NOT NULL COMMENT '套餐编号',
    `expire_time`     datetime    NOT NULL COMMENT '过期时间',
    `account_count`   int         NOT NULL DEFAULT 0 COMMENT '账号数量限制',
    `creator`         varchar(32) NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         varchar(32)          DEFAULT '' COMMENT '更新者',
    `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         tinyint (1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    -- 增加索引提高查询效率
    KEY               `idx_package_id` (`package_id`),
    KEY               `idx_expire_time` (`expire_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租户表';