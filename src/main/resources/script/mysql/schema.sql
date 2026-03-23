-- =================================================================================
-- 1. 租户套餐表
-- =================================================================================
CREATE TABLE `sys_tenant_package`
(
    `id`           bigint      NOT NULL COMMENT '主键',
    `package_name` varchar(50) NOT NULL COMMENT '套餐名称',
    `menu_ids`     text        NOT NULL COMMENT '关联菜单权限 ID 集合',
    `status`       tinyint              DEFAULT '0' COMMENT '状态（0 正常 1 停用）',
    `remark`       varchar(500)         DEFAULT NULL COMMENT '备注',
    -- BaseEntity 字段
    `create_by`    varchar(64)          DEFAULT '' COMMENT '创建者',
    `create_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    varchar(64)          DEFAULT '' COMMENT '更新者',
    `update_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      tinyint     NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户套餐表';

-- =================================================================================
-- 2. 租户表
-- =================================================================================
CREATE TABLE `sys_tenant`
(
    `id`            bigint      NOT NULL COMMENT '租户 ID',
    `parent_id`     bigint               DEFAULT '0' COMMENT '上级租户 ID',
    `package_id`    bigint               DEFAULT NULL COMMENT '关联套餐 ID',
    `tenant_name`   varchar(50) NOT NULL COMMENT '租户名称',
    `tenant_type`   varchar(10) NOT NULL COMMENT '租户类型：P, S, B',
    `tenant_path`   varchar(255)         DEFAULT NULL COMMENT '租户链路溯源',
    `contact_user`  varchar(20)          DEFAULT NULL COMMENT '联系人',
    `contact_phone` varchar(11)          DEFAULT NULL COMMENT '联系电话',
    `status`        tinyint              DEFAULT '0' COMMENT '租户状态',
    `expire_time`   datetime             DEFAULT NULL COMMENT '过期时间',
    -- BaseEntity 字段
    `create_by`     varchar(64)          DEFAULT '' COMMENT '创建者',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     varchar(64)          DEFAULT '' COMMENT '更新者',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       tinyint     NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY             `idx_parent_id` (`parent_id`),
    KEY             `idx_tenant_path` (`tenant_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- =================================================================================
-- 3. 菜单权限表
-- =================================================================================
CREATE TABLE `sys_menu`
(
    `id`          bigint      NOT NULL COMMENT '菜单 ID',
    `menu_name`   varchar(50) NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint               DEFAULT '0' COMMENT '父菜单 ID',
    `order_num`   int                  DEFAULT '0' COMMENT '显示顺序',
    `path`        varchar(200)         DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255)         DEFAULT NULL COMMENT '组件路径',
    `perms`       varchar(100)         DEFAULT NULL COMMENT '权限标识',
    `menu_type`   char(1)              DEFAULT '' COMMENT '类型（M 目录 C 菜单 F 按钮）',
    `icon`        varchar(100)         DEFAULT '#' COMMENT '图标',
    `tenant_type` varchar(10)          DEFAULT 'ALL' COMMENT '适用对象',
    -- BaseEntity 字段
    `create_by`   varchar(64)          DEFAULT '' COMMENT '创建者',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64)          DEFAULT '' COMMENT '更新者',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     tinyint     NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- =================================================================================
-- 4. 角色表
-- =================================================================================
CREATE TABLE `sys_role`
(
    `id`          bigint       NOT NULL COMMENT '角色 ID',
    `tenant_id`   bigint       NOT NULL COMMENT '所属租户',
    `role_name`   varchar(30)  NOT NULL COMMENT '角色名称',
    `role_key`    varchar(100) NOT NULL COMMENT '角色权限字符串',
    `data_scope`  char(1)               DEFAULT '1' COMMENT '数据范围',
    `status`      tinyint      NOT NULL DEFAULT '0' COMMENT '状态',
    -- BaseEntity 字段
    `create_by`   varchar(64)           DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64)           DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY           `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =================================================================================
-- 5. 用户表
-- =================================================================================
CREATE TABLE `sys_user`
(
    `id`          bigint       NOT NULL COMMENT '用户 ID',
    `tenant_id`   bigint       NOT NULL COMMENT '所属租户 ID',
    `username`    varchar(30)  NOT NULL COMMENT '登录账号',
    `nickname`    varchar(30)           DEFAULT '' COMMENT '用户昵称',
    `password`    varchar(100) NOT NULL COMMENT '密码',
    `user_type`   varchar(10)           DEFAULT 'SYS' COMMENT '用户类型',
    `dept_id`     bigint                DEFAULT NULL COMMENT '部门 ID',
    `status`      tinyint               DEFAULT '0' COMMENT '状态',
    -- BaseEntity 字段
    `create_by`   varchar(64)           DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64)           DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username_tenant` (`username`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 6. 用户与角色关联 (多对多)
CREATE TABLE `sys_user_role`
(
    `user_id`     bigint   NOT NULL COMMENT '用户 ID',
    `role_id`     bigint   NOT NULL COMMENT '角色 ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 7. 角色与菜单关联 (多对多)
CREATE TABLE `sys_role_menu`
(
    `role_id`     bigint   NOT NULL COMMENT '角色 ID',
    `menu_id`     bigint   NOT NULL COMMENT '菜单 ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 8. 消费者表 (C端用户信息)
CREATE TABLE `sys_consumer`
(
    `id`              bigint      NOT NULL COMMENT '消费者 ID',
    `union_id`        varchar(64)          DEFAULT NULL COMMENT '微信开放平台唯一标识',
    `openid`          varchar(64)          DEFAULT NULL COMMENT '小程序/公众号唯一标识',
    `phone`           varchar(11) NOT NULL COMMENT '手机号',
    `nickname`        varchar(30)          DEFAULT '' COMMENT '昵称',
    `avatar`          varchar(255)         DEFAULT '' COMMENT '头像',
    `gender`          tinyint              DEFAULT '0' COMMENT '性别 (0未知 1男 2女)',
    `last_login_time` datetime             DEFAULT NULL COMMENT '最后登录时间',
    -- BaseEntity 字段
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         tinyint     NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY               `idx_union_id` (`union_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费者信息表';

-- 9. 消费者与商家关系（C2B 会员体系）
CREATE TABLE `rel_consumer_business`
(
    `consumer_id`        bigint   NOT NULL COMMENT '消费者 ID',
    `business_tenant_id` bigint   NOT NULL COMMENT '归属商家租户 ID',
    `member_level`       int               DEFAULT '0' COMMENT '在该商家的会员等级',
    `points`             int               DEFAULT '0' COMMENT '在该商家的积分',
    `status`             tinyint           DEFAULT '0' COMMENT '会员状态 (0正常 1冻结)',
    -- 审计字段
    `join_time`          datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入商家时间',
    `update_time`        datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '信息更新时间',
    PRIMARY KEY (`consumer_id`, `business_tenant_id`),
    KEY                  `idx_business_tenant` (`business_tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费者商家关联表';

-- =================================================================================
-- 10. 审计日志表
-- =================================================================================
CREATE TABLE `audit_log`
(
    `id`          bigint       NOT NULL COMMENT '主键 ID',
    `tenant_id`   bigint       NOT NULL DEFAULT '0' COMMENT '所属租户 ID',
    `module`      varchar(64)  NOT NULL DEFAULT '' COMMENT '功能模块',
    `operation`   varchar(128) NOT NULL DEFAULT '' COMMENT '操作描述',
    `business_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '业务主键',
    `operator`    varchar(64)  NOT NULL DEFAULT '' COMMENT '操作人账号',
    `cost_time`   int unsigned    DEFAULT '0' COMMENT '耗时 (毫秒)',
    `status`      varchar(20)  NOT NULL DEFAULT 'SUCCESS' COMMENT '状态',
    `error_msg`   text COMMENT '异常堆栈信息',
    `changes`     json                  DEFAULT NULL COMMENT '变更明细',
    -- 审计日志通常不建议逻辑删除，这里仅加时间戳
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY           `idx_tenant_module` (`tenant_id`, `module`),
    KEY           `idx_business_id` (`business_id`),
    KEY           `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';