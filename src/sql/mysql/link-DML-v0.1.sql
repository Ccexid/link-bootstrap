-- ====================================================================
-- 种子数据 DML 脚本(link-DDL-v0.1 配套)
-- ====================================================================
-- 用途:为 link 平台填充初始的菜单/权限/角色/用户/组织/租户数据,
--      方便本地开发与登录测试。手动执行,不通过 Spring Boot 自动加载。
--
-- 执行前提:link-DDL-v0.1.sql 已建表完毕,数据库默认字符集 utf8mb4。
-- 重复执行:本脚本所有 INSERT 都使用显式主键 ID,重复执行会因唯一索引报错。
--          重置环境时先依次 TRUNCATE 业务表(脚本末尾给了模板,默认注释)。
--
-- ====================================================================
-- 密码说明(已预填,可直接执行)
-- ====================================================================
-- 所有种子用户的初始密码统一为:Admin@123456
-- system_users 表 INSERT 时已经填好对应的 BCrypt 哈希:
--     $2a$10$fl.Cigg977fX.8rT/XtmEuKBtaYFJN/A.G7UMewQH0C2.ymOOSvJC
-- (满足当前用户服务的 8-64 字符密码规则,兼容 hutool 内置 jBCrypt 实现)
--
-- 手机号说明:
-- system_users/system_tenant/system_organization 不再保存明文手机号。
-- 种子数据只预置脱敏展示值,密文和检索哈希由应用服务在用户录入真实手机号时生成。
--
-- 如要修改默认密码,重新生成哈希:
--   macOS / Linux:
--     htpasswd -bnBC 10 "" "新密码" | tr -d ':\n' | sed 's/\$2y\$/\$2a\$/'
--   或在 IDEA Java Scratch 里跑(项目已引入 hutool):
--     System.out.println(cn.hutool.crypto.digest.BCrypt.hashpw("新密码", cn.hutool.crypto.digest.BCrypt.gensalt()));
-- 然后把输出的哈希全文替换下方 system_users 各行的 password 字段。
--
-- ====================================================================
-- 登录测试账号一览(初始密码均 Admin@123456)
-- ====================================================================
--   | username | tenantId | 角色          | 用途                       |
--   |----------|----------|---------------|----------------------------|
--   | root     |        0 | super_admin   | 跨租户超管,LinkTenantLine  |
--   |          |          |               | Handler 自动放行隔离       |
--   | platform |        0 | platform_admin| 平台运营,管租户/套餐       |
--   | admin    |        1 | tenant_admin  | 租户 1 管理员              |
--   | user1    |        1 | tenant_user   | 租户 1 普通用户(只读)    |
--   | admin    |        2 | tenant_admin  | 租户 2 管理员              |
--
-- 调用样例:
--   curl -X POST http://localhost:48080/api/v1/auth/tokens \
--     -H 'Content-Type: application/json' \
--     -d '{"username":"root","password":"Admin@123456","tenantId":0}'
-- ====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. 租户套餐 system_tenant_package
-- ====================================================================
INSERT INTO `system_tenant_package` (`id`, `name`, `status`, `remark`, `menu_ids`, `creator`, `updater`)
VALUES
    (1, '基础套餐',   0, '默认套餐,包含用户/角色/菜单/组织/操作日志/社区基础内容', JSON_ARRAY(100,110,120,130,140,150,160,300,310), 1, 1),
    (2, '高级套餐',   0, '高级套餐,额外含租户管理菜单',                         JSON_ARRAY(100,110,120,130,140,150,160,200,210,300,310), 1, 1);

-- ====================================================================
-- 2. 租户 system_tenant
-- ====================================================================
-- 注:平台租户(tenant_id=0)不在本表中,它仅作为默认值代表"平台自身"。
INSERT INTO `system_tenant` (`id`, `name`, `contact_user_id`, `contact_name`, `contact_mobile_cipher`, `contact_mobile_hash`, `contact_mobile_mask`, `contact_mobile_key_version`, `status`, `websites`, `package_id`, `expire_time`, `account_count`, `creator`, `updater`)
VALUES
    (1, '示例租户A', 3, '示例租户A联系人', NULL, NULL, NULL, 1, 0, JSON_ARRAY('demo-a.link.local'),  1, '2099-12-31 23:59:59', 100, 1, 1),
    (2, '示例租户B', 5, '示例租户B联系人', NULL, NULL, NULL, 1, 0, JSON_ARRAY('demo-b.link.local'),  2, '2099-12-31 23:59:59', 100, 1, 1);

-- ====================================================================
-- 3. 菜单 system_menu (全局表,无 tenant_id)
-- 命名约定:权限码 = system:{module}:{action},与 @SaCheckPermission 注解一致
-- 类型 type: 1=目录 2=菜单 3=按钮
-- ====================================================================

-- ---------- 3.1 系统管理(目录) ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (1, '系统管理', '', 1, 10, 0, '/system', 'system', NULL, NULL, 0, 0, 0, 0, 1, 1);

-- ---------- 3.2 用户管理 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (100, '用户管理', 'system:user:list',   2, 10, 1,   'user',  'user',  'system/user/index', 'SystemUser', 0, 0, 0, 0, 1, 1),
    (101, '用户创建', 'system:user:create', 3, 10, 100, '',      '',      NULL,                NULL,        0, 0, 0, 0, 1, 1),
    (102, '用户查询', 'system:user:query',  3, 20, 100, '',      '',      NULL,                NULL,        0, 0, 0, 0, 1, 1),
    (103, '用户更新', 'system:user:update', 3, 30, 100, '',      '',      NULL,                NULL,        0, 0, 0, 0, 1, 1),
    (104, '用户删除', 'system:user:delete', 3, 40, 100, '',      '',      NULL,                NULL,        0, 0, 0, 0, 1, 1);

-- ---------- 3.3 角色管理 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (110, '角色管理', 'system:role:list',   2, 20, 1,   'role',  'role',  'system/role/index', 'SystemRole', 0, 0, 0, 0, 1, 1),
    (111, '角色创建', 'system:role:create', 3, 10, 110, '',      '',      NULL,                NULL,         0, 0, 0, 0, 1, 1),
    (112, '角色查询', 'system:role:query',  3, 20, 110, '',      '',      NULL,                NULL,         0, 0, 0, 0, 1, 1),
    (113, '角色更新', 'system:role:update', 3, 30, 110, '',      '',      NULL,                NULL,         0, 0, 0, 0, 1, 1),
    (114, '角色删除', 'system:role:delete', 3, 40, 110, '',      '',      NULL,                NULL,         0, 0, 0, 0, 1, 1);

-- ---------- 3.4 菜单管理 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (120, '菜单管理', 'system:menu:list',   2, 30, 1,   'menu',  'menu',  'system/menu/index', 'SystemMenu', 0, 0, 0, 0, 1, 1),
    (121, '菜单创建', 'system:menu:create', 3, 10, 120, '',      '',      NULL,                NULL,         0, 0, 0, 0, 1, 1),
    (122, '菜单查询', 'system:menu:query',  3, 20, 120, '',      '',      NULL,                NULL,         0, 0, 0, 0, 1, 1),
    (123, '菜单更新', 'system:menu:update', 3, 30, 120, '',      '',      NULL,                NULL,         0, 0, 0, 0, 1, 1),
    (124, '菜单删除', 'system:menu:delete', 3, 40, 120, '',      '',      NULL,                NULL,         0, 0, 0, 0, 1, 1);

-- ---------- 3.5 组织管理 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (130, '组织管理', 'system:organization:list',   2, 40, 1,   'org',   'org',   'system/organization/index', 'SystemOrganization', 0, 0, 0, 0, 1, 1),
    (131, '组织创建', 'system:organization:create', 3, 10, 130, '',      '',      NULL,                        NULL,                 0, 0, 0, 0, 1, 1),
    (132, '组织查询', 'system:organization:query',  3, 20, 130, '',      '',      NULL,                        NULL,                 0, 0, 0, 0, 1, 1),
    (133, '组织更新', 'system:organization:update', 3, 30, 130, '',      '',      NULL,                        NULL,                 0, 0, 0, 0, 1, 1),
    (134, '组织删除', 'system:organization:delete', 3, 40, 130, '',      '',      NULL,                        NULL,                 0, 0, 0, 0, 1, 1);

-- ---------- 3.6 用户角色关联 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (140, '用户角色', 'system:user-role:list',   2, 50, 1,   'user-role', 'link', 'system/userRole/index', 'SystemUserRole', 0, 0, 0, 0, 1, 1),
    (141, '关联创建', 'system:user-role:create', 3, 10, 140, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1),
    (142, '关联查询', 'system:user-role:query',  3, 20, 140, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1),
    (143, '关联更新', 'system:user-role:update', 3, 30, 140, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1),
    (144, '关联删除', 'system:user-role:delete', 3, 40, 140, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1),
    (145, '批量分配', 'system:user-role:assign', 3, 50, 140, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1);

-- ---------- 3.7 角色菜单关联 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (150, '角色菜单', 'system:role-menu:list',      2, 60, 1,   'role-menu', 'link', 'system/roleMenu/index', 'SystemRoleMenu', 0, 0, 0, 0, 1, 1),
    (151, '关联创建', 'system:role-menu:create',    3, 10, 150, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1),
    (152, '关联查询', 'system:role-menu:query',     3, 20, 150, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1),
    (153, '关联更新', 'system:role-menu:update',    3, 30, 150, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1),
    (154, '关联删除', 'system:role-menu:delete',    3, 40, 150, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1),
    (155, '批量授权', 'system:role-menu:authorize', 3, 50, 150, '',          '',     NULL,                    NULL,             0, 0, 0, 0, 1, 1);

-- ---------- 3.8 操作日志 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (160, '操作日志', 'system:operate-log:list',   2, 70, 1,   'log',   'log',   'system/operateLog/index', 'SystemOperateLog', 0, 0, 0, 0, 1, 1),
    (162, '日志查询', 'system:operate-log:query',  3, 20, 160, '',      '',      NULL,                      NULL,               0, 0, 0, 0, 1, 1);

-- ---------- 3.9 租户管理(平台超管功能,目录) ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (2, '租户运营', '', 1, 20, 0, '/tenant', 'tenant', NULL, NULL, 0, 0, 0, 0, 1, 1);

-- ---------- 3.10 租户管理菜单 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (200, '租户管理', 'system:tenant:list',   2, 10, 2,   'tenant',         'tenant',  'tenant/index',        'TenantIndex',   0, 0, 0, 0, 1, 1),
    (201, '租户创建', 'system:tenant:create', 3, 10, 200, '',               '',        NULL,                  NULL,            0, 0, 0, 0, 1, 1),
    (202, '租户查询', 'system:tenant:query',  3, 20, 200, '',               '',        NULL,                  NULL,            0, 0, 0, 0, 1, 1),
    (203, '租户更新', 'system:tenant:update', 3, 30, 200, '',               '',        NULL,                  NULL,            0, 0, 0, 0, 1, 1),
    (204, '租户删除', 'system:tenant:delete', 3, 40, 200, '',               '',        NULL,                  NULL,            0, 0, 0, 0, 1, 1);

-- ---------- 3.11 租户套餐 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (210, '租户套餐', 'system:tenant-package:list',   2, 20, 2,   'package',  'package', 'tenant/package/index', 'TenantPackage', 0, 0, 0, 0, 1, 1),
    (211, '套餐创建', 'system:tenant-package:create', 3, 10, 210, '',         '',        NULL,                   NULL,            0, 0, 0, 0, 1, 1),
    (212, '套餐查询', 'system:tenant-package:query',  3, 20, 210, '',         '',        NULL,                   NULL,            0, 0, 0, 0, 1, 1),
    (213, '套餐更新', 'system:tenant-package:update', 3, 30, 210, '',         '',        NULL,                   NULL,            0, 0, 0, 0, 1, 1),
    (214, '套餐删除', 'system:tenant-package:delete', 3, 40, 210, '',         '',        NULL,                   NULL,            0, 0, 0, 0, 1, 1);

-- ---------- 3.12 社区运营 ----------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `updater`)
VALUES
    (3,   '社区运营', '',                              1, 30, 0,   '/community', 'community', NULL,                       NULL,               0, 0, 0, 0, 1, 1),
    (300, '板块管理', 'system:community:section:list', 2, 10, 3,   'sections',   'list',      'community/section/index',  'CommunitySection', 0, 0, 0, 0, 1, 1),
    (301, '板块创建', 'system:community:section:create', 3, 10, 300, '',         '',          NULL,                       NULL,               0, 0, 0, 0, 1, 1),
    (302, '板块查询', 'system:community:section:query',  3, 20, 300, '',         '',          NULL,                       NULL,               0, 0, 0, 0, 1, 1),
    (303, '板块更新', 'system:community:section:update', 3, 30, 300, '',         '',          NULL,                       NULL,               0, 0, 0, 0, 1, 1),
    (304, '板块删除', 'system:community:section:delete', 3, 40, 300, '',         '',          NULL,                       NULL,               0, 0, 0, 0, 1, 1),
    (310, '话题管理', 'system:community:topic:list',     2, 20, 3,   'topics',     'topic',     'community/topic/index',    'CommunityTopic',   0, 0, 0, 0, 1, 1),
    (311, '话题创建', 'system:community:topic:create',   3, 10, 310, '',          '',          NULL,                       NULL,               0, 0, 0, 0, 1, 1),
    (312, '话题查询', 'system:community:topic:query',    3, 20, 310, '',          '',          NULL,                       NULL,               0, 0, 0, 0, 1, 1),
    (313, '话题更新', 'system:community:topic:update',   3, 30, 310, '',          '',          NULL,                       NULL,               0, 0, 0, 0, 1, 1),
    (314, '话题删除', 'system:community:topic:delete',   3, 40, 310, '',          '',          NULL,                       NULL,               0, 0, 0, 0, 1, 1);

-- ====================================================================
-- 4. 角色 system_role
-- ====================================================================
-- 平台租户(tenant_id=0)
INSERT INTO `system_role` (`id`, `name`, `code`, `sort`, `data_scope`, `data_scope_dept_ids`, `status`, `type`, `remark`, `tenant_id`, `creator`, `updater`)
VALUES
    (1, '超级管理员', 'super_admin',    0, 1, '', 0, 1, '平台超级管理员,跨租户访问', 0, 1, 1),
    (2, '平台运营',   'platform_admin', 1, 1, '', 0, 1, '平台租户与套餐管理',         0, 1, 1);

-- 租户 1
INSERT INTO `system_role` (`id`, `name`, `code`, `sort`, `data_scope`, `data_scope_dept_ids`, `status`, `type`, `remark`, `tenant_id`, `creator`, `updater`)
VALUES
    (3, '租户管理员', 'tenant_admin', 0, 1, '', 0, 1, '租户内全部权限', 1, 1, 1),
    (4, '普通用户',   'tenant_user',  1, 3, '', 0, 1, '租户内只读',     1, 1, 1);

-- 租户 2
INSERT INTO `system_role` (`id`, `name`, `code`, `sort`, `data_scope`, `data_scope_dept_ids`, `status`, `type`, `remark`, `tenant_id`, `creator`, `updater`)
VALUES
    (5, '租户管理员', 'tenant_admin', 0, 1, '', 0, 1, '租户内全部权限', 2, 1, 1);

-- ====================================================================
-- 5. 用户 system_users
-- ====================================================================
-- 所有用户的 password 字段统一为 BCrypt 哈希,明文密码 Admin@123456(见文件头部说明)
INSERT INTO `system_users` (`id`, `username`, `password`, `nickname`, `user_type`, `mobile_cipher`, `mobile_hash`, `mobile_mask`, `mobile_key_version`, `email`, `avatar`, `status`, `org_id`, `dept_id`, `login_ip`, `login_date`, `tenant_id`, `creator`, `updater`)
VALUES
    (1, 'root',     '$2a$10$fl.Cigg977fX.8rT/XtmEuKBtaYFJN/A.G7UMewQH0C2.ymOOSvJC', '系统超管',     2, NULL, NULL, '138****0001', 1, 'root@link.local',       '', 0, NULL, NULL, '', NULL, 0, 1, 1),
    (2, 'platform', '$2a$10$fl.Cigg977fX.8rT/XtmEuKBtaYFJN/A.G7UMewQH0C2.ymOOSvJC', '平台运营',     2, NULL, NULL, '138****0002', 1, 'platform@link.local',   '', 0, NULL, NULL, '', NULL, 0, 1, 1),
    (3, 'admin',    '$2a$10$fl.Cigg977fX.8rT/XtmEuKBtaYFJN/A.G7UMewQH0C2.ymOOSvJC', '租户A管理员',  3, NULL, NULL, '138****0003', 1, 'admin@demo-a.link.local', '', 0, NULL, NULL, '', NULL, 1, 1, 1),
    (4, 'user1',    '$2a$10$fl.Cigg977fX.8rT/XtmEuKBtaYFJN/A.G7UMewQH0C2.ymOOSvJC', '租户A用户',    4, NULL, NULL, '138****0004', 1, 'user1@demo-a.link.local', '', 0, NULL, NULL, '', NULL, 1, 1, 1),
    (5, 'admin',    '$2a$10$fl.Cigg977fX.8rT/XtmEuKBtaYFJN/A.G7UMewQH0C2.ymOOSvJC', '租户B管理员',  3, NULL, NULL, '138****0005', 1, 'admin@demo-b.link.local', '', 0, NULL, NULL, '', NULL, 2, 1, 1);

-- ====================================================================
-- 6. 用户-角色 system_user_role
-- ====================================================================
INSERT INTO `system_user_role` (`id`, `user_id`, `role_id`, `tenant_id`, `creator`, `updater`)
VALUES
    (1, 1, 1, 0, 1, 1),   -- root      → super_admin
    (2, 2, 2, 0, 1, 1),   -- platform  → platform_admin
    (3, 3, 3, 1, 1, 1),   -- admin@1   → tenant_admin
    (4, 4, 4, 1, 1, 1),   -- user1@1   → tenant_user
    (5, 5, 5, 2, 1, 1);   -- admin@2   → tenant_admin

-- ====================================================================
-- 7. 角色-菜单 system_role_menu
-- ====================================================================
-- 7.1 super_admin (role=1, tenant=0) 拥有【全部菜单】
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `tenant_id`, `creator`, `updater`)
SELECT 1, m.`id`, 0, 1, 1 FROM `system_menu` m WHERE m.`deleted` = 0;

-- 7.2 platform_admin (role=2, tenant=0) 拥有【租户/套餐】+ 用户/角色/菜单查看
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `tenant_id`, `creator`, `updater`)
SELECT 2, m.`id`, 0, 1, 1 FROM `system_menu` m
WHERE m.`deleted` = 0
  AND m.`id` IN (
    -- 租户管理
    2, 200, 201, 202, 203, 204,
    -- 租户套餐
    210, 211, 212, 213, 214,
    -- 平台用户/角色查看(无修改)
    1, 100, 102, 110, 112,
    -- 操作日志
    160, 162
  );

-- 7.3 tenant_admin (role=3, tenant=1) 拥有租户内全部业务菜单(不含租户管理)
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `tenant_id`, `creator`, `updater`)
SELECT 3, m.`id`, 1, 1, 1 FROM `system_menu` m
WHERE m.`deleted` = 0
  AND m.`id` IN (
    1,
    100, 101, 102, 103, 104,
    110, 111, 112, 113, 114,
    120, 121, 122, 123, 124,
    130, 131, 132, 133, 134,
    140, 141, 142, 143, 144, 145,
    150, 151, 152, 153, 154, 155,
    160, 162,
    3, 300, 301, 302, 303, 304, 310, 311, 312, 313, 314
  );

-- 7.4 tenant_user (role=4, tenant=1) 仅查看类菜单
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `tenant_id`, `creator`, `updater`)
SELECT 4, m.`id`, 1, 1, 1 FROM `system_menu` m
WHERE m.`deleted` = 0
  AND m.`id` IN (1, 100, 102, 110, 112, 120, 122, 130, 132, 160, 162, 3, 300, 302, 310, 312);

-- 7.5 tenant_admin (role=5, tenant=2) 同 role=3
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `tenant_id`, `creator`, `updater`)
SELECT 5, m.`id`, 2, 1, 1 FROM `system_menu` m
WHERE m.`deleted` = 0
  AND m.`id` IN (
    1,
    100, 101, 102, 103, 104,
    110, 111, 112, 113, 114,
    120, 121, 122, 123, 124,
    130, 131, 132, 133, 134,
    140, 141, 142, 143, 144, 145,
    150, 151, 152, 153, 154, 155,
    160, 162,
    3, 300, 301, 302, 303, 304, 310, 311, 312, 313, 314
  );

-- ====================================================================
-- 8. 社区板块 community_section (示例)
-- ====================================================================
INSERT INTO `community_section` (`id`, `name`, `code`, `description`, `cover_url`, `parent_id`, `sort`, `status`, `tenant_id`, `creator`, `updater`)
VALUES
    (1, '步行街',   'street',     '日常闲聊、生活经验和社区公共讨论', NULL, 0, 10, 0, 1, 3, 3),
    (2, '篮球讨论', 'basketball', '篮球赛事、球员、战术和装备讨论',     NULL, 0, 20, 0, 1, 3, 3),
    (3, '游戏电竞', 'gaming',     '游戏、电竞赛事和玩家交流',           NULL, 0, 30, 0, 1, 3, 3),
    (4, '数码产品', 'digital',    '手机、电脑、外设和数码消费讨论',     NULL, 0, 40, 0, 1, 3, 3),
    (5, '步行街',   'street',     '租户B日常闲聊和社区公共讨论',        NULL, 0, 10, 0, 2, 5, 5),
    (6, '篮球讨论', 'basketball', '租户B篮球赛事与球迷交流',            NULL, 0, 20, 0, 2, 5, 5);

-- ====================================================================
-- 9. 社区话题 community_topic (示例)
-- ====================================================================
INSERT INTO `community_topic` (`id`, `section_id`, `name`, `code`, `description`, `cover_url`, `sort`, `status`, `tenant_id`, `creator`, `updater`)
VALUES
    (1, 1, '每日闲聊', 'daily-chat', '步行街日常交流话题',        NULL, 10, 0, 1, 3, 3),
    (2, 2, 'NBA',      'nba',        'NBA 赛事与球员讨论',        NULL, 10, 0, 1, 3, 3),
    (3, 2, 'CBA',      'cba',        'CBA 赛事与本土篮球讨论',    NULL, 20, 0, 1, 3, 3),
    (4, 3, '英雄联盟', 'lol',        '英雄联盟赛事和玩家讨论',    NULL, 10, 0, 1, 3, 3),
    (5, 5, '每日闲聊', 'daily-chat', '租户B步行街日常交流话题',   NULL, 10, 0, 2, 5, 5),
    (6, 6, 'NBA',      'nba',        '租户B NBA 赛事与球员讨论',  NULL, 10, 0, 2, 5, 5);

-- ====================================================================
-- 10. 社区帖子 community_post (示例)
-- ====================================================================
INSERT INTO `community_post` (`id`, `section_id`, `topic_id`, `author_id`, `title`, `summary`, `content`, `cover_url`, `view_count`, `like_count`, `comment_count`, `collect_count`, `pinned`, `featured`, `status`, `tenant_id`, `creator`, `updater`)
VALUES
    (1, 1, 1, 4, '步行街每日闲聊帖', '示例租户A的步行街闲聊帖子', '这里是步行街每日闲聊帖正文，用于本地联调帖子列表和详情。', NULL, 120, 12, 2, 3, 1, 0, 0, 1, 4, 4),
    (2, 2, 2, 4, '今天的 NBA 比赛怎么看', '示例租户A的篮球讨论帖子', '这里是 NBA 比赛讨论正文，后续可接入评论、点赞和收藏。', NULL, 80, 8, 1, 1, 0, 1, 0, 1, 4, 4),
    (3, 5, 5, 5, '租户B步行街开帖', '示例租户B的步行街闲聊帖子', '这里是租户B社区帖子正文，用于验证租户隔离。', NULL, 20, 1, 0, 0, 0, 0, 0, 2, 5, 5);

-- ====================================================================
-- 11. 社区评论 community_comment (示例)
-- ====================================================================
INSERT INTO `community_comment` (`id`, `post_id`, `parent_id`, `root_id`, `author_id`, `reply_to_id`, `content`, `like_count`, `reply_count`, `status`, `tenant_id`, `creator`, `updater`)
VALUES
    (1, 1, 0, 0, 4, NULL, '这是步行街帖子的一楼评论。', 3, 1, 0, 1, 4, 4),
    (2, 1, 1, 1, 4, 4, '这是对一楼评论的回复。', 1, 0, 0, 1, 4, 4),
    (3, 2, 0, 0, 4, NULL, '这场比赛第四节很关键。', 2, 0, 0, 1, 4, 4);

-- ====================================================================
-- 12. 组织 system_organization (示例)
-- ====================================================================
INSERT INTO `system_organization` (`id`, `name`, `org_type`, `parent_id`, `ancestors`, `level`, `contact_name`, `contact_mobile_cipher`, `contact_mobile_hash`, `contact_mobile_mask`, `contact_mobile_key_version`, `status`, `tenant_id`, `creator`, `updater`)
VALUES
    (1, '示例集团A',  3, 0, '0',   1, '示例联系人',    NULL, NULL, '138****1000', 1, 0, 1, 1, 1),
    (2, '研发部',     3, 1, '0,1', 2, '研发负责人',    NULL, NULL, '138****1001', 1, 0, 1, 3, 3),
    (3, '示例集团B',  3, 0, '0',   1, '示例B联系人',   NULL, NULL, '138****2000', 1, 0, 2, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================================
-- 附录:重置脚本(默认注释,需要重新跑种子时取消注释)
-- ====================================================================
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE `system_role_menu`;
-- TRUNCATE TABLE `system_user_role`;
-- TRUNCATE TABLE `community_section`;
-- TRUNCATE TABLE `system_organization`;
-- TRUNCATE TABLE `system_users`;
-- TRUNCATE TABLE `system_role`;
-- TRUNCATE TABLE `system_menu`;
-- TRUNCATE TABLE `system_tenant`;
-- TRUNCATE TABLE `system_tenant_package`;
-- SET FOREIGN_KEY_CHECKS = 1;
