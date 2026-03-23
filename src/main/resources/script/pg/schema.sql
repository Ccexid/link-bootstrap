-- =================================================================================
-- PostgreSQL Schema
-- =================================================================================

-- 1. 租户套餐表
CREATE TABLE sys_tenant_package
(
    id           bigint                   NOT NULL,
    package_name varchar(50)              NOT NULL,
    menu_ids     text                     NOT NULL,
    status       smallint                          DEFAULT 0,
    remark       varchar(500),
    create_by    varchar(64)                       DEFAULT '',
    create_time  timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by    varchar(64)                       DEFAULT '',
    update_time  timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      smallint                 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT
ON TABLE sys_tenant_package IS '租户套餐表';

-- 2. 租户表
CREATE TABLE sys_tenant
(
    id            bigint                   NOT NULL,
    parent_id     bigint                            DEFAULT 0,
    package_id    bigint,
    tenant_name   varchar(50)              NOT NULL,
    tenant_type   varchar(10)              NOT NULL,
    tenant_path   varchar(255),
    contact_user  varchar(20),
    contact_phone varchar(11),
    status        smallint                          DEFAULT 0,
    expire_time   timestamp WITH time zone,
    create_by     varchar(64)                       DEFAULT '',
    create_time   timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by     varchar(64)                       DEFAULT '',
    update_time   timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       smallint                 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX idx_tenant_parent_id ON sys_tenant (parent_id);
CREATE INDEX idx_tenant_path ON sys_tenant (tenant_path);

-- 3. 菜单权限表
CREATE TABLE sys_menu
(
    id          bigint                   NOT NULL,
    menu_name   varchar(50)              NOT NULL,
    parent_id   bigint                            DEFAULT 0,
    order_num   integer                           DEFAULT 0,
    path        varchar(200)                      DEFAULT '',
    component   varchar(255),
    perms       varchar(100),
    menu_type   char(1)                           DEFAULT '',
    icon        varchar(100)                      DEFAULT '#',
    tenant_type varchar(10)                       DEFAULT 'ALL',
    create_by   varchar(64)                       DEFAULT '',
    create_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   varchar(64)                       DEFAULT '',
    update_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     smallint                 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 4. 角色表
CREATE TABLE sys_role
(
    id          bigint                   NOT NULL,
    tenant_id   bigint                   NOT NULL,
    role_name   varchar(30)              NOT NULL,
    role_key    varchar(100)             NOT NULL,
    data_scope  char(1)                           DEFAULT '1',
    status      smallint                 NOT NULL DEFAULT 0,
    create_by   varchar(64)                       DEFAULT '',
    create_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   varchar(64)                       DEFAULT '',
    update_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     smallint                 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX idx_role_tenant_id ON sys_role (tenant_id);

-- 5. 用户表
CREATE TABLE sys_user
(
    id          bigint                   NOT NULL,
    tenant_id   bigint                   NOT NULL,
    username    varchar(30)              NOT NULL,
    nickname    varchar(30)                       DEFAULT '',
    password    varchar(100)             NOT NULL,
    user_type   varchar(10)                       DEFAULT 'SYS',
    dept_id     bigint,
    status      smallint                          DEFAULT 0,
    create_by   varchar(64)                       DEFAULT '',
    create_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   varchar(64)                       DEFAULT '',
    update_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     smallint                 NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_username_tenant UNIQUE (username, tenant_id)
);

-- 6. 用户与角色关联
CREATE TABLE sys_user_role
(
    user_id     bigint NOT NULL,
    role_id     bigint NOT NULL,
    create_time timestamp WITH time zone DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

-- 7. 角色与菜单关联
CREATE TABLE sys_role_menu
(
    role_id     bigint NOT NULL,
    menu_id     bigint NOT NULL,
    create_time timestamp WITH time zone DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, menu_id)
);

-- 8. 消费者表
CREATE TABLE sys_consumer
(
    id          bigint                   NOT NULL,
    union_id    varchar(64),
    phone       varchar(11)              NOT NULL,
    nickname    varchar(30)                       DEFAULT '',
    avatar      varchar(255)                      DEFAULT '',
    create_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     smallint                          DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_consumer_phone UNIQUE (phone)
);

-- 9. 消费者与商家关系
CREATE TABLE rel_consumer_business
(
    consumer_id        bigint                   NOT NULL,
    business_tenant_id bigint                   NOT NULL,
    member_level       integer                           DEFAULT 0,
    points             integer                           DEFAULT 0,
    status             smallint                          DEFAULT 0,
    join_time          timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (consumer_id, business_tenant_id)
);

-- 10. 审计日志表
CREATE TABLE audit_log
(
    id          bigint                   NOT NULL,
    tenant_id   bigint                   NOT NULL DEFAULT 0,
    module      varchar(64)              NOT NULL DEFAULT '',
    operation   varchar(128)             NOT NULL DEFAULT '',
    business_id varchar(64)              NOT NULL DEFAULT '',
    operator    varchar(64)              NOT NULL DEFAULT '',
    cost_time   integer                           DEFAULT 0,
    status      varchar(20)              NOT NULL DEFAULT 'SUCCESS',
    error_msg   text,
    changes     jsonb, -- 使用 JSONB 优化查询
    create_time timestamp WITH time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
CREATE INDEX idx_audit_tenant_module ON audit_log (tenant_id, module);
CREATE INDEX idx_audit_business_id ON audit_log (business_id);
CREATE INDEX idx_audit_create_time ON audit_log (create_time);