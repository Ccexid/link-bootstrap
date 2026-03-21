-- 审计日志表
CREATE TABLE IF NOT EXISTS audit_log
(
    id
    BIGINT
    PRIMARY
    KEY,
    tenant_id
    VARCHAR
(
    50
),
    module VARCHAR
(
    100
),
    operation VARCHAR
(
    200
),
    business_id VARCHAR
(
    100
),
    operator VARCHAR
(
    100
),
    cost_time VARCHAR
(
    50
),
    status VARCHAR
(
    20
),
    error_msg TEXT,
    changes TEXT, -- H2 中通常用 TEXT 或 JSON 存储
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );