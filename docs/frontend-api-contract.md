# 前端接口对接文档

本文档根据当前 `src/main/java/me/link/bootstrap/interfaces` 与相关 Web 配置生成，用于前端联调。接口统一前缀为 `/api/v1`。

## 通用约定

### 请求

- `Content-Type`: `application/json`，分页查询使用 URL query 参数。
- 认证头由登录响应返回的 `token_name` 和 `token_prefix` 决定，当前通常为 `Authorization: Bearer <token_value>`。
- 请求和响应 JSON 字段统一为 `snake_case`。例如 Java 字段 `pageNo` 对外为 `page_no`。
- `Long` 和 `long` 类型响应会序列化为字符串，前端不要强转为 JS number。
- `LocalDateTime` 格式为 `yyyy-MM-dd HH:mm:ss`。
- `StatusEnum` 响应值为中文描述：`正常`、`停用`。请求侧如使用枚举，建议按后端当前枚举反序列化能力联调确认。

### 分页与排序

分页接口统一使用 query 参数：

| 参数 | 必填 | 说明 |
|---|---|---|
| `page_no` | 是 | 页码，从 1 开始，默认 1 |
| `page_size` | 是 | 每页条数，1-200，默认 10 |
| `sort` | 否 | 多字段排序，逗号分隔；字段前加 `-` 表示降序，例如 `-created_at,id` |

分页响应会返回 `sortable_fields`，前端排序字段应以该数组为准。

### 统一成功响应

单对象或无数据响应：

```json
{
  "data": {},
  "message": "操作成功",
  "code": 0,
  "timestamp": 1710000000000,
  "trace_id": "trace-id"
}
```

分页响应：

```json
{
  "records": [],
  "total": "0",
  "code": 0,
  "timestamp": 1710000000000,
  "trace_id": "trace-id",
  "sortable_fields": ["id", "created_at"]
}
```

### 错误响应

错误也使用统一结构。部分业务异常 HTTP 状态可能仍为 200，前端应以 `code !== 0` 判断业务失败。

| 场景 | HTTP 状态 | code | 说明 |
|---|---:|---:|---|
| 参数校验失败 | 400 | 400000001 | `message` 为具体校验错误 |
| 未登录或 Token 失效 | 401 | 401000001 | 需要重新登录 |
| 无权限 | 403 | 403000001 | 已登录但缺少权限 |
| 资源不存在 | 404 | 404000001 | 路径或静态资源不存在 |
| 方法不允许 | 405 | 405000001 | HTTP method 不匹配 |
| 重复提交 | 429 | 429000001 | 幂等拦截 |
| 限流 | 429 | 429000002 | 操作过于频繁 |
| 系统异常 | 500 | 500999999 | 查看后端日志 |

### 接口加密

当 `link.api-crypto.enabled=true` 时，匹配 `/api/v1/**` 的接口启用 RSA 加解密，以下路径排除：`/api/v1/auth/public-keys/current`、Actuator、Swagger。

- 请求体加密格式：`{ "data": "RSA密文" }`
- 查询参数加密格式：`?data=RSA密文`，密文解开后必须是 JSON 对象。
- 响应加密格式：`{ "data": "RSA密文" }`，响应头包含 `X-Encrypted: RSA`。
- 首次握手公钥接口：`GET /api/v1/auth/public-keys/current`，匿名可访问。

## 认证接口

基础路径：`/api/v1/auth`。以下 4 个入口匿名可访问：创建账号密码 Token、创建邮箱验证码 Token、发送邮箱验证码、获取公钥。刷新、查询、删除当前 Token 需要登录。

| 方法 | 路径 | 鉴权 | 限流 | 请求 | 响应 |
|---|---|---|---|---|---|
| POST | `/tokens` | 匿名 | 同用户名 60 秒 5 次 | `LoginRequest` | `TokenResponseVO` |
| POST | `/email-code-tokens` | 匿名 | 同邮箱 60 秒 5 次 | `EmailLoginRequest` | `TokenResponseVO` |
| POST | `/email-verification-codes` | 匿名 | 同邮箱 60 秒 1 次 | `SendEmailCodeRequest` | 无数据 |
| PUT | `/tokens/current` | 登录 | - | 无 | `TokenResponseVO` |
| GET | `/tokens/current` | 登录 | - | 无 | `TokenResponseVO` |
| GET | `/public-keys/current` | 匿名 | - | 无 | `ApiCryptoPublicKeyResponseVO` |
| DELETE | `/tokens/current` | 登录 | - | 无 | 无数据 |

`LoginRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `username` | string | 是 | 用户名，2-30 |
| `password` | string | 是 | 明文密码，8-64 |
| `captcha_token` | string | 否 | 开启人机校验后必填 |

`EmailLoginRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `email` | string | 是 | 邮箱 |
| `code` | string | 是 | 邮箱验证码，4-8 |
| `captcha_token` | string | 否 | 开启人机校验后必填 |

`SendEmailCodeRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `email` | string | 是 | 邮箱 |
| `captcha_token` | string | 否 | 开启人机校验后必填 |

`TokenResponseVO`

| 字段 | 类型 | 说明 |
|---|---|---|
| `token_name` | string | 请求头名称，通常为 `Authorization` |
| `token_value` | string | Token 值，不含前缀 |
| `token_prefix` | string | Token 前缀，通常为 `Bearer` |
| `token_timeout` | string | Token 剩余有效期，秒 |
| `token_active_timeout` | string | Token 剩余无操作有效期，秒 |

## 租户管理

基础路径：`/api/v1/system/tenants`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/tenants` | `system:tenant:create` | `TenantCreateRequest` | `TenantResponseVO` |
| GET | `/api/v1/system/tenants/{id}` | `system:tenant:query` | path: `id` | `TenantResponseVO` |
| GET | `/api/v1/system/tenants` | `system:tenant:list` | `TenantPageRequest` query | `ResultTableResponse<TenantResponseVO>` |
| PUT | `/api/v1/system/tenants/{id}` | `system:tenant:update` | `TenantUpdateRequest` | `TenantResponseVO` |
| DELETE | `/api/v1/system/tenants/{id}` | `system:tenant:delete` | path: `id` | 无数据 |

`TenantCreateRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 租户名称 |
| `contact_user_id` | string | 否 | 联系人用户编号 |
| `contact_name` | string | 是 | 联系人姓名 |
| `contact_mobile` | string | 否 | 完整联系手机，后端加密、哈希并脱敏 |
| `websites` | string[] | 否 | 绑定域名数组 |
| `package_id` | string | 是 | 租户套餐编号 |
| `expire_time` | string | 是 | 过期时间，必须晚于当前时间 |
| `account_count` | number | 是 | 账号数量，最小 1 |

`TenantUpdateRequest`: `contact_user_id`、`contact_name`、`contact_mobile`、`websites`、`package_id`、`expire_time`、`account_count` 同创建；额外 `enabled` boolean 可选。

`TenantPageRequest`: 通用分页参数 + `name`。

`TenantResponseVO`: `id`、`name`、`contact_user_id`、`contact_name`、`contact_mobile`、`status`、`websites`、`package_id`、`expire_time`、`account_count`、`created_at`、`updated_at`。

## 租户套餐

基础路径：`/api/v1/system/tenant-packages`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/tenant-packages` | `system:tenant-package:create` | `TenantPackageCreateRequest` | `TenantPackageResponseVO` |
| GET | `/api/v1/system/tenant-packages/{id}` | `system:tenant-package:query` | path: `id` | `TenantPackageResponseVO` |
| GET | `/api/v1/system/tenant-packages` | `system:tenant-package:list` | `TenantPackagePageRequest` query | 分页 |
| PUT | `/api/v1/system/tenant-packages/{id}` | `system:tenant-package:update` | `TenantPackageUpdateRequest` | `TenantPackageResponseVO` |
| DELETE | `/api/v1/system/tenant-packages/{id}` | `system:tenant-package:delete` | path: `id` | 无数据 |

请求字段：

| DTO | 字段 |
|---|---|
| `TenantPackageCreateRequest` | `name` 必填，`remark` 可选，`menu_ids` 必填非空 |
| `TenantPackageUpdateRequest` | `name` 必填，`remark` 可选，`menu_ids` 必填非空，`enabled` 可选 |
| `TenantPackagePageRequest` | 通用分页参数 + `name` |

响应字段：`id`、`name`、`status`、`remark`、`menu_ids`、`created_at`、`updated_at`。

## 用户管理

基础路径：`/api/v1/system/users`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/users` | `system:user:create` | `UserCreateRequest` | `UserResponseVO` |
| GET | `/api/v1/system/users/{id}` | `system:user:query` | path: `id` | `UserResponseVO` |
| GET | `/api/v1/system/users` | `system:user:list` | `UserPageRequest` query | 分页 |
| PUT | `/api/v1/system/users/{id}` | `system:user:update` | `UserUpdateRequest` | `UserResponseVO` |
| DELETE | `/api/v1/system/users/{id}` | `system:user:delete` | path: `id` | 无数据 |

`UserCreateRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `username` | string | 是 | 用户账号，同租户唯一，2-30 |
| `password` | string | 是 | 明文密码，8-64 |
| `nickname` | string | 是 | 用户昵称 |
| `user_type` | number | 是 | 1供应商 2平台 3商家 4用户 |
| `mobile` | string | 是 | 完整手机号码，后端加密、哈希并脱敏 |
| `email` | string | 否 | 邮箱 |
| `avatar` | string | 否 | 头像地址 |
| `status` | string/number | 否 | 账号状态 |
| `org_id` | string | 否 | 所属组织编号 |
| `dept_id` | string | 否 | 平台内部部门编号 |
| `login_ip` | string | 否 | 后端自动填写，前端可不传 |
| `login_date` | string | 否 | 后端自动填写，前端可不传 |

`UserUpdateRequest`: 与创建基本一致；`password` 可选，为空保留原密码。

`UserPageRequest`: 通用分页参数 + `username`、`nickname`、`mobile`、`email`、`user_type`、`status`。

响应字段：`id`、`username`、`nickname`、`user_type`、`mobile`、`email`、`avatar`、`status`、`org_id`、`dept_id`、`login_ip`、`login_date`、`tenant_id`、`created_at`、`updated_at`。

## 角色管理

基础路径：`/api/v1/system/roles`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/roles` | `system:role:create` | `RoleCreateRequest` | `RoleResponseVO` |
| GET | `/api/v1/system/roles/{id}` | `system:role:query` | path: `id` | `RoleResponseVO` |
| GET | `/api/v1/system/roles` | `system:role:list` | `RolePageRequest` query | 分页 |
| PUT | `/api/v1/system/roles/{id}` | `system:role:update` | `RoleUpdateRequest` | `RoleResponseVO` |
| DELETE | `/api/v1/system/roles/{id}` | `system:role:delete` | path: `id` | 无数据 |

请求字段：

| DTO | 字段 |
|---|---|
| `RoleCreateRequest` | `name` 必填，`code` 必填，`sort` 必填，`data_scope`、`data_scope_dept_ids`、`status` 可选，`type` 必填，`remark` 可选 |
| `RoleUpdateRequest` | `name`、`code`、`sort`、`data_scope`、`data_scope_dept_ids`、`status`、`type`、`remark` 均可选 |
| `RolePageRequest` | 通用分页参数 + `name`、`code`、`status`、`type` |

响应字段：`id`、`name`、`code`、`sort`、`data_scope`、`data_scope_dept_ids`、`status`、`type`、`remark`、`tenant_id`、`created_at`、`updated_at`。

## 菜单管理

基础路径：`/api/v1/system/menus`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/menus` | `system:menu:create` | `MenuCreateRequest` | `MenuResponseVO` |
| GET | `/api/v1/system/menus/{id}` | `system:menu:query` | path: `id` | `MenuResponseVO` |
| GET | `/api/v1/system/menus` | `system:menu:list` | `MenuPageRequest` query | 分页 |
| PUT | `/api/v1/system/menus/{id}` | `system:menu:update` | `MenuUpdateRequest` | `MenuResponseVO` |
| DELETE | `/api/v1/system/menus/{id}` | `system:menu:delete` | path: `id` | 无数据 |

请求字段：

| DTO | 字段 |
|---|---|
| `MenuCreateRequest` | `name` 必填，`permission` 可选，`type` 必填，`sort`、`parent_id`、`path`、`icon`、`component`、`component_name`、`status`、`visible`、`keep_alive`、`always_show` 可选 |
| `MenuUpdateRequest` | `name`、`permission`、`type`、`sort`、`parent_id`、`path`、`icon`、`component`、`component_name`、`status`、`visible`、`keep_alive`、`always_show` 均可选 |
| `MenuPageRequest` | 通用分页参数 + `name`、`permission`、`type`、`parent_id`、`status` |

响应字段：`id`、`name`、`permission`、`type`、`sort`、`parent_id`、`path`、`icon`、`component`、`component_name`、`status`、`visible`、`keep_alive`、`always_show`、`created_at`、`updated_at`。

## 组织管理

基础路径：`/api/v1/system/organizations`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/organizations` | `system:organization:create` | `OrganizationCreateRequest` | `OrganizationResponseVO` |
| GET | `/api/v1/system/organizations/{id}` | `system:organization:query` | path: `id` | `OrganizationResponseVO` |
| GET | `/api/v1/system/organizations` | `system:organization:list` | `OrganizationPageRequest` query | 分页 |
| PUT | `/api/v1/system/organizations/{id}` | `system:organization:update` | `OrganizationUpdateRequest` | `OrganizationResponseVO` |
| DELETE | `/api/v1/system/organizations/{id}` | `system:organization:delete` | path: `id` | 无数据 |

请求字段：

| DTO | 字段 |
|---|---|
| `OrganizationCreateRequest` | `name` 必填，`org_type` 必填，`parent_id`、`ancestors`、`level`、`contact_name`、`contact_mobile`、`status` 可选 |
| `OrganizationUpdateRequest` | `name`、`org_type`、`parent_id`、`ancestors`、`level`、`contact_name`、`contact_mobile`、`status` 均可选 |
| `OrganizationPageRequest` | 通用分页参数 + `name`、`org_type`、`parent_id`、`status` |

响应字段：`id`、`name`、`org_type`、`parent_id`、`ancestors`、`level`、`contact_name`、`contact_mobile`、`status`、`tenant_id`、`created_at`、`updated_at`。

## 用户角色关联

基础路径：`/api/v1/system/user-roles`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/user-roles` | `system:user-role:create` | `UserRoleCreateRequest` | `UserRoleResponseVO` |
| POST | `/api/v1/system/user-roles/assign` | `system:user-role:assign` | `UserRoleAssignRequest` | 无数据 |
| GET | `/api/v1/system/user-roles/{id}` | `system:user-role:query` | path: `id` | `UserRoleResponseVO` |
| GET | `/api/v1/system/user-roles` | `system:user-role:list` | `UserRolePageRequest` query | 分页 |
| PUT | `/api/v1/system/user-roles/{id}` | `system:user-role:update` | `UserRoleUpdateRequest` | `UserRoleResponseVO` |
| DELETE | `/api/v1/system/user-roles/{id}` | `system:user-role:delete` | path: `id` | 无数据 |

请求字段：

| DTO | 字段 |
|---|---|
| `UserRoleCreateRequest` | `user_id`、`role_id` |
| `UserRoleUpdateRequest` | `user_id`、`role_id` |
| `UserRoleAssignRequest` | `user_id`、`role_ids` |
| `UserRolePageRequest` | 通用分页参数 + `user_id`、`role_id` |

响应字段：`id`、`user_id`、`role_id`、`tenant_id`、`created_at`、`updated_at`。

## 角色菜单关联

基础路径：`/api/v1/system/role-menus`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/role-menus` | `system:role-menu:create` | `RoleMenuCreateRequest` | `RoleMenuResponseVO` |
| POST | `/api/v1/system/role-menus/authorize` | `system:role-menu:authorize` | `RoleMenuAuthorizeRequest` | 无数据 |
| GET | `/api/v1/system/role-menus/{id}` | `system:role-menu:query` | path: `id` | `RoleMenuResponseVO` |
| GET | `/api/v1/system/role-menus` | `system:role-menu:list` | `RoleMenuPageRequest` query | 分页 |
| PUT | `/api/v1/system/role-menus/{id}` | `system:role-menu:update` | `RoleMenuUpdateRequest` | `RoleMenuResponseVO` |
| DELETE | `/api/v1/system/role-menus/{id}` | `system:role-menu:delete` | path: `id` | 无数据 |

请求字段：

| DTO | 字段 |
|---|---|
| `RoleMenuCreateRequest` | `role_id`、`menu_id` |
| `RoleMenuUpdateRequest` | `role_id`、`menu_id` |
| `RoleMenuAuthorizeRequest` | `role_id`、`menu_ids` |
| `RoleMenuPageRequest` | 通用分页参数 + `role_id`、`menu_id` |

响应字段：`id`、`role_id`、`menu_id`、`tenant_id`、`created_at`、`updated_at`。

## 操作日志

基础路径：`/api/v1/system/operate-logs`。该模块只提供审计查询，不提供前端写入接口。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| GET | `/api/v1/system/operate-logs/{id}` | `system:operate-log:query` | path: `id` | `OperateLogResponseVO` |
| GET | `/api/v1/system/operate-logs` | `system:operate-log:list` | `OperateLogPageRequest` query | 分页 |

`OperateLogPageRequest`: 通用分页参数 + `trace_id`、`user_id`、`module`、`operation`、`biz_id`、`success`。

响应字段：`id`、`trace_id`、`user_id`、`user_type`、`user_ip`、`user_agent`、`module`、`operation`、`biz_id`、`action`、`extra`、`success`、`request_method`、`request_url`、`duration`、`tenant_id`、`created_at`、`updated_at`。

## 社区板块后台管理

基础路径：`/api/v1/system/community/sections`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/community/sections` | `system:community:section:create` | `CommunitySectionCreateRequest` | `CommunitySectionResponseVO` |
| GET | `/api/v1/system/community/sections/{id}` | `system:community:section:query` | path: `id` | `CommunitySectionResponseVO` |
| GET | `/api/v1/system/community/sections` | `system:community:section:list` | `CommunitySectionPageRequest` query | 分页 |
| PUT | `/api/v1/system/community/sections/{id}` | `system:community:section:update` | `CommunitySectionUpdateRequest` | `CommunitySectionResponseVO` |
| DELETE | `/api/v1/system/community/sections/{id}` | `system:community:section:delete` | path: `id` | 无数据 |

`CommunitySectionCreateRequest` / `CommunitySectionUpdateRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 板块名称，最长 60 |
| `code` | string | 是 | 板块编码，2-64，仅字母、数字、下划线、中划线 |
| `description` | string | 否 | 板块描述，最长 500 |
| `cover_url` | string | 否 | 封面地址，最长 512 |
| `parent_id` | string | 否 | 父级板块 ID，0 表示顶级，最小 0 |
| `sort` | number | 否 | 排序值，最小 0 |
| `status` | string/number | 否 | 状态 |

`CommunitySectionPageRequest`: 通用分页参数 + `name`、`code`、`parent_id`、`status`。

响应字段：`id`、`name`、`code`、`description`、`cover_url`、`parent_id`、`sort`、`status`、`created_at`、`updated_at`。

## 社区话题后台管理

基础路径：`/api/v1/system/community/topics`。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/system/community/topics` | `system:community:topic:create` | `CommunityTopicCreateRequest` | `CommunityTopicResponseVO` |
| GET | `/api/v1/system/community/topics/{id}` | `system:community:topic:query` | path: `id` | `CommunityTopicResponseVO` |
| GET | `/api/v1/system/community/topics` | `system:community:topic:list` | `CommunityTopicPageRequest` query | 分页 |
| PUT | `/api/v1/system/community/topics/{id}` | `system:community:topic:update` | `CommunityTopicUpdateRequest` | `CommunityTopicResponseVO` |
| DELETE | `/api/v1/system/community/topics/{id}` | `system:community:topic:delete` | path: `id` | 无数据 |

`CommunityTopicCreateRequest` / `CommunityTopicUpdateRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `section_id` | string | 是 | 所属板块 ID，必须大于 0 且归属当前租户 |
| `name` | string | 是 | 话题名称，最长 80 |
| `code` | string | 是 | 话题编码，2-80，仅字母、数字、下划线、中划线；同租户唯一 |
| `description` | string | 否 | 话题描述，最长 500 |
| `cover_url` | string | 否 | 封面地址，最长 512 |
| `sort` | number | 否 | 排序值，最小 0 |
| `status` | string/number | 否 | 状态 |

`CommunityTopicPageRequest`: 通用分页参数 + `section_id`、`name`、`code`、`status`。

响应字段：`id`、`section_id`、`name`、`code`、`description`、`cover_url`、`sort`、`status`、`created_at`、`updated_at`。

## 社区帖子用户端

基础路径：`/api/v1/community/posts`。当前版本受全局登录拦截保护，需要登录后访问；后续如开放匿名浏览，应同步调整登录白名单和接口加密策略。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/community/posts` | 登录即可 | `CommunityPostCreateRequest` | `CommunityPostResponseVO` |
| GET | `/api/v1/community/posts/{id}` | 登录即可 | path: `id` | `CommunityPostResponseVO` |
| GET | `/api/v1/community/posts` | 登录即可 | `CommunityPostPageRequest` query | 分页 |
| PUT | `/api/v1/community/posts/{id}` | 本人帖子 | `CommunityPostUpdateRequest` | `CommunityPostResponseVO` |
| DELETE | `/api/v1/community/posts/{id}` | 本人帖子 | path: `id` | 无数据 |

`CommunityPostCreateRequest` / `CommunityPostUpdateRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `section_id` | string | 是 | 所属板块 ID，必须大于 0 且归属当前租户 |
| `topic_id` | string | 否 | 所属话题 ID；传入时必须归属当前租户和所属板块 |
| `title` | string | 是 | 帖子标题，最长 120 |
| `summary` | string | 否 | 摘要，最长 300 |
| `content` | string | 是 | 正文，最长 20000 |
| `cover_url` | string | 否 | 封面地址，最长 512 |

`CommunityPostPageRequest`: 通用分页参数 + `section_id`、`topic_id`、`author_id`、`title`、`status`。

响应字段：`id`、`section_id`、`topic_id`、`author_id`、`title`、`summary`、`content`、`cover_url`、`view_count`、`like_count`、`comment_count`、`collect_count`、`pinned`、`featured`、`status`、`created_at`、`updated_at`。

## 社区帖子互动用户端

基础路径：`/api/v1/community/posts`。点赞和收藏接口为幂等语义，重复点赞/收藏不会报错；取消未点赞/未收藏状态也不会报错。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/community/posts/{id}/likes` | 登录即可 | path: `id` | `CommunityPostInteractionResponseVO` |
| DELETE | `/api/v1/community/posts/{id}/likes` | 登录即可 | path: `id` | `CommunityPostInteractionResponseVO` |
| POST | `/api/v1/community/posts/{id}/collections` | 登录即可 | path: `id` | `CommunityPostInteractionResponseVO` |
| DELETE | `/api/v1/community/posts/{id}/collections` | 登录即可 | path: `id` | `CommunityPostInteractionResponseVO` |
| GET | `/api/v1/community/posts/{id}/interactions/current` | 登录即可 | path: `id` | `CommunityPostInteractionResponseVO` |

`CommunityPostInteractionResponseVO` 字段：`liked`、`collected`、`like_count`、`collect_count`。

## 社区评论用户端

基础路径：`/api/v1/community/comments`。当前版本受全局登录拦截保护，需要登录后访问；如后续开放只读评论列表，应同步调整登录白名单。

| 方法 | 路径 | 权限码 | 请求 | 响应 |
|---|---|---|---|---|
| POST | `/api/v1/community/comments` | 登录即可 | `CommunityCommentCreateRequest` | `CommunityCommentResponseVO` |
| GET | `/api/v1/community/comments/{id}` | 登录即可 | path: `id` | `CommunityCommentResponseVO` |
| GET | `/api/v1/community/comments` | 登录即可 | `CommunityCommentPageRequest` query | 分页 |
| PUT | `/api/v1/community/comments/{id}` | 本人评论 | `CommunityCommentUpdateRequest` | `CommunityCommentResponseVO` |
| DELETE | `/api/v1/community/comments/{id}` | 本人评论 | path: `id` | 无数据 |

`CommunityCommentCreateRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `post_id` | string | 是 | 所属帖子 ID，必须大于 0 且归属当前租户 |
| `parent_id` | string | 否 | 父评论 ID；0 或空表示一级评论，传入时必须属于同一帖子 |
| `content` | string | 是 | 评论内容，最长 2000 |

`CommunityCommentUpdateRequest`

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `content` | string | 是 | 评论内容，最长 2000 |

`CommunityCommentPageRequest`: 通用分页参数 + `post_id`、`root_id`、`author_id`、`status`。查询一级评论传 `root_id=0`；查询某条一级评论下的回复传 `root_id=一级评论ID`。

响应字段：`id`、`post_id`、`parent_id`、`root_id`、`author_id`、`reply_to_id`、`content`、`like_count`、`reply_count`、`status`、`created_at`、`updated_at`。

## 前端联调建议

1. 登录后保存 `token_name`、`token_prefix`、`token_value`，所有非匿名接口按返回值组装认证头。
2. 所有接口先判断 HTTP 状态，再判断业务 `code`；`code=0` 才表示业务成功。
3. 列表页排序字段使用分页响应的 `sortable_fields`，不要在前端硬编码未确认字段。
4. 手机号等敏感字段创建/更新传明文，响应只使用后端返回的脱敏值。
5. 开启接口加密后，除公钥接口外，前端看到的业务响应会被包进加密 `data` 字段，需要先解密再按本文档解析。
