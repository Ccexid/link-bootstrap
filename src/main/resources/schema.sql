-- =================================================================================
-- H2 Schema (Mode=MySQL/PostgreSQL compatible)
-- =================================================================================

-- 1. 租户套餐表
CREATE TABLE sys_tenant_package
(
    id           BIGINT      NOT NULL,
    package_name VARCHAR(50) NOT NULL,
    menu_ids     CLOB        NOT NULL,
    status       SMALLINT             DEFAULT 0,
    remark       VARCHAR(500),
    create_by    VARCHAR(64)          DEFAULT '',
    create_time  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by    VARCHAR(64)          DEFAULT '',
    update_time  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      SMALLINT             DEFAULT 0,
    PRIMARY KEY (id)
);

-- 2. 租户表
CREATE TABLE sys_tenant
(
    id            BIGINT      NOT NULL,
    parent_id     BIGINT               DEFAULT 0,
    package_id    BIGINT,
    tenant_name   VARCHAR(50) NOT NULL,
    tenant_type   VARCHAR(10) NOT NULL,
    tenant_path   VARCHAR(255),
    contact_user  VARCHAR(20),
    contact_phone VARCHAR(11),
    status        SMALLINT             DEFAULT 0,
    expire_time   TIMESTAMP,
    create_by     VARCHAR(64)          DEFAULT '',
    create_time   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by     VARCHAR(64)          DEFAULT '',
    update_time   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       SMALLINT             DEFAULT 0,
    PRIMARY KEY (id)
);

-- 3. 菜单权限表
CREATE TABLE sys_menu
(
    id          BIGINT      NOT NULL,
    menu_name   VARCHAR(50) NOT NULL,
    parent_id   BIGINT               DEFAULT 0,
    order_num   INT                  DEFAULT 0,
    path        VARCHAR(200)         DEFAULT '',
    component   VARCHAR(255),
    perms       VARCHAR(100),
    menu_type   CHAR(1)              DEFAULT '',
    icon        VARCHAR(100)         DEFAULT '#',
    tenant_type VARCHAR(10)          DEFAULT 'ALL',
    create_by   VARCHAR(64)          DEFAULT '',
    create_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64)          DEFAULT '',
    update_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT             DEFAULT 0,
    PRIMARY KEY (id)
);

-- 4. 角色表
CREATE TABLE sys_role
(
    id          BIGINT       NOT NULL,
    tenant_id   BIGINT       NOT NULL,
    role_name   VARCHAR(30)  NOT NULL,
    role_key    VARCHAR(100) NOT NULL,
    data_scope  CHAR(1)               DEFAULT '1',
    status      SMALLINT              DEFAULT 0,
    create_by   VARCHAR(64)           DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64)           DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT              DEFAULT 0,
    PRIMARY KEY (id)
);

-- 5. 用户表
CREATE TABLE sys_user
(
    id          BIGINT       NOT NULL,
    tenant_id   BIGINT       NOT NULL,
    username    VARCHAR(30)  NOT NULL,
    nickname    VARCHAR(30)           DEFAULT '',
    password    VARCHAR(100) NOT NULL,
    user_type   VARCHAR(10)           DEFAULT 'SYS',
    dept_id     BIGINT,
    status      SMALLINT              DEFAULT 0,
    create_by   VARCHAR(64)           DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   VARCHAR(64)           DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT              DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_username_tenant UNIQUE (username, tenant_id)
);


-- 8. 消费者表 (H2 语法)
CREATE TABLE sys_consumer
(
    id          BIGINT PRIMARY KEY,
    union_id    VARCHAR(64),
    phone       VARCHAR(11) NOT NULL UNIQUE,
    nickname    VARCHAR(30),
    avatar      VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT   DEFAULT 0
);

-- 9. 消费者商家关联表
CREATE TABLE rel_consumer_business
(
    consumer_id        BIGINT NOT NULL,
    business_tenant_id BIGINT NOT NULL,
    member_level       INT       DEFAULT 0,
    points             INT       DEFAULT 0,
    join_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (consumer_id, business_tenant_id)
);

-- 10. 审计日志表
CREATE TABLE audit_log
(
    id          BIGINT    NOT NULL,
    tenant_id   BIGINT             DEFAULT 0,
    module      VARCHAR(64)        DEFAULT '',
    operation   VARCHAR(128)       DEFAULT '',
    business_id VARCHAR(64)        DEFAULT '',
    operator    VARCHAR(64)        DEFAULT '',
    cost_time   INT                DEFAULT 0,
    status      VARCHAR(20)        DEFAULT 'SUCCESS',
    error_msg   CLOB,
    changes     JSON, -- H2 2.x 支持原生 JSON 类型
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);