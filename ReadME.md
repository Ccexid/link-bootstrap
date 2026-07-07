# Link Bootstrap

`link-bootstrap` 是一个面向类虎扑社区平台的 Spring Boot 3 后端基础工程。项目秉持**极简主义**，摒弃臃肿的 DDD 样板代码，采用经典的 `Controller -> Service -> Mapper` 三层架构，严格遵循 Google Java Style、标准 RESTful 规范和 Spring Boot 最佳实践。

## 目录

- [技术栈](#技术栈)
- [极简架构规范](#极简架构规范)
- [RESTful 接口规范](#restful-接口规范)
- [代码与命名规范](#代码与命名规范)
- [持久化与数据规范](#持久化与数据规范)
- [安全与多租户规范](#安全与多租户规范)
- [异常与统一响应](#异常与统一响应)
- [配置与运行](#配置与运行)

## 技术栈

| 分类 | 技术 | 说明 |
|---|---|---|
| JDK | Java | 17 |
| 框架 | Spring Boot | 3.5.x |
| Web 容器 | Undertow | 替代默认 Tomcat，提升性能 |
| ORM | MyBatis-Plus | 简化 CRUD，提供代码生成器 |
| 数据库 | MySQL | 结构维护在 `src/sql/mysql/link-DDL-v0.1.sql` |
| 权限框架 | Sa-Token | 轻量级权限认证 |
| 缓存 | Redisson | 分布式锁与缓存 |
| Bean 映射 | MapStruct | 编译期生成转换代码，性能零损耗 |
| API 文档 | SpringDoc OpenAPI | Swagger UI |
| 构建工具 | Maven Wrapper | 推荐使用 `./mvnw` |

## 极简架构规范

项目坚决抵制过度设计，统一采用极简三层架构，减少不必要的转发和样板代码。

### 1. 调用链路

```text
XxxController  ->  XxxService  ->  XxxMapper  ->  XxxPO
```

### 2. 层级职责

- **Controller (`interfaces/controller/`)**: 只负责 HTTP 路由、`@Valid` 参数校验、调用 Service 和包装统一响应。**禁止**写业务逻辑、拼接 SQL。

- **Service (`application/service/`)**: 直接继承 MyBatis-Plus 的 `IService` 和 `ServiceImpl`。承载所有业务逻辑、事务控制（`@Transactional`）、PO 与 DTO 的转换。**禁止**直接暴露 PO 给前端。
- **Mapper (`infrastructure/mapper/`)**: 继承 `BaseMapper`，只负责数据库交互。复杂 SQL 写在对应的 XML 中。
- **PO (`infrastructure/persistence/`)**: 纯粹的数据库映射对象，**禁止**包含任何业务行为方法。

### 3. 严禁生成的冗余文件

除非业务复杂度达到领域模型升级标准，否则**禁止**生成以下文件：
`Entity`, `Factory`, `Repository`, `RepositoryImpl`, `Command`, `Query`, `InternalService`, `Converter`。

## RESTful 接口规范

严格遵循 RESTful 架构风格，使用 HTTP 动词语义化操作资源。为保证前后端联调效率与拦截器统一处理，业务响应统一采用 HTTP 200 配合业务 `code` 的方式。

### 1. 路径与动作

- **统一前缀**：`/api/v1`
- **资源命名**：使用复数名词，如 `/api/v1/users`、`/api/v1/community/posts`。
- **标准动作**：
  - `GET /resources`：分页或列表查询。
  - `GET /resources/{id}`：查询单个详情。
  - `POST /resources`：创建资源。
  - `PUT /resources/{id}`：整体更新资源。
  - `DELETE /resources/{id}`：删除资源。
- **非 CRUD 动作**：允许使用动词路径，如 `POST /api/v1/system/community/posts/{id}/audit`。

### 2. HTTP 状态码约定

- `200 OK`：所有业务成功响应（无论是否有返回数据，统一返回包装体）。
- `400 Bad Request`：参数校验失败。
- `401 Unauthorized`：未登录。
- `403 Forbidden`：无权限。
- `404 Not Found`：接口路由不存在。
- `500 Internal Server Error`：系统内部异常。

## 代码与命名规范

融合 Google Java Style Guide 与 Spring Boot 最佳实践。

### 1. 命名规范

| 类型 | 命名规范 | 示例 |
|---|---|---|
| Controller | `XxxController` | `UserController` |
| Service | `XxxService` / `XxxServiceImpl` | `UserService` |
| Mapper | `XxxMapper` | `UserMapper` |
| 请求 DTO | `XxxRequest` | `UserCreateRequest` |
| 响应 VO | `XxxResponseVO` | `UserResponseVO` |
| 持久化对象 | `XxxPO` | `UserPO` |

### 2. Spring Boot 规范

- **依赖注入**：统一使用 `@RequiredArgsConstructor` 进行构造器注入，**禁止**使用 `@Autowired` 字段注入。
- **参数校验**：在 Controller 方法参数上使用 `@Valid`，在 DTO 字段上使用 Jakarta Validation 注解（如 `@NotBlank`, `@NotNull`）。
- **对象转换**：在 Service 层使用 MapStruct 或明确的私有方法进行对象转换，Controller 只负责接收和响应。

### 3. Google Java 规范

- **方法长度**：单方法尽量不超过 80 行，避免深层嵌套，善用卫语句提前 `return`。
- **注释规范**：类和复杂业务方法必须有 Javadoc。**禁止**生成“获取用户信息”、“设置 ID”等废话注释，变量名应自解释。
- **常量管理**：全局常量放入 `GlobalConstants`，避免魔法值散落。

## 持久化与数据规范

### 1. PO 规范

- PO 仅表示数据库表结构，必须通过 `@TableName` 显式声明表名。
- 公共审计字段（如 `created_at`, `updated_at`, `is_deleted`）通过继承 `BaseDO` 获取。
- 需要租户隔离的 PO 继承 `TenantBaseDO`。
- 数据库关键字字段必须用反引号保护，如 `` @TableField("`status`") ``。

### 2. SQL 脚本

- 数据库结构唯一维护在：`src/sql/mysql/link-DDL-v0.1.sql`。
- 菜单、权限、种子数据唯一维护在：`src/sql/mysql/link-DML-v0.1.sql`。
- 禁止将业务表结构写入 `schema.sql` 或通过 Java 初始化器写入。

### 3. 分页与排序

- 分页结果统一返回 `PageResult<T>`。
- 前端排序参数格式：`sort=-created_at,name`（降序加 `-`）。
- 禁止直接将前端字段拼接进 SQL，必须在 Service 层维护排序字段白名单映射。

## 安全与多租户规范

### 1. 认证与权限

- 全局登录态由 Sa-Token 拦截，白名单仅限登录、公钥、Swagger 等必要入口。
- 业务写接口必须添加 `@SaCheckPermission("resource:action")`。
- 高频或易滥用接口必须添加 `@RateLimit`。
- 创建、更新等非幂等写操作添加 `@Idempotent`。

### 2. 多端访问边界

- **平台端**：`/api/v1/system/**`，使用 `system:*:*` 权限码。
- **运营端**：`/api/v1/system/community/**`，使用 `system:community:*:*` 权限码。
- **用户端**：`/api/v1/community/**`，使用 `community:*:*` 权限码。公开浏览可匿名，写操作必须校验登录态。
- 只有超级管理员或标注 `@TenantIgnore` 的方法可跨租户访问，且 `@TenantIgnore` 必须贴在最小方法范围并说明原因。

## 异常与统一响应

### 1. 统一响应契约

- 不在 Controller 手动拼装失败响应，统一抛出 `BusinessException` 并附带 `ErrorCode`，由全局异常处理器接管。
- 返回值统一包装为 `ResultResponse<T>` 或 `ResultTableResponse<T>`。

单对象响应：

```json
{
  "data": {},
  "message": "操作成功",
  "code": 0,
  "timestamp": 1710000000000,
  "trace_id": "xxxx"
}
```

分页响应：

```json
{
  "records": [],
  "total": 0,
  "code": 0,
  "timestamp": 1710000000000,
  "trace_id": "xxxx",
  "sortable_fields": ["id", "created_at"]
}
```

*(注：JSON 全局使用 `SNAKE_CASE`，`Long` 序列化为 String 防止前端精度丢失，默认过滤 `null` 字段。)*

### 2. 异常处理

- 业务异常统一抛出 `BusinessException` 并附带 `ErrorCode`。
- 禁止在 Controller 中写 try-catch，统一由 `@RestControllerAdvice` 全局异常处理器拦截并转换为统一响应。
- 禁止向前端暴露系统内部堆栈细节。

## 配置与运行

### 1. 环境配置

| 环境 | 配置文件 | 说明 |
|---|---|---|
| dev | `application-dev.yml` | 开发环境，默认激活，可包含本地 H2/Swagger |
| test | `application-test.yml` | 测试环境，敏感配置由环境变量注入 |
| prod | `application-prod.yml` | 生产环境，配置中心注入 |

### 2. 常用命令

**本地启动：**

```bash
./mvnw spring-boot:run
```

**编译（提交前必做）：**

```bash
./mvnw -q -DskipTests compile
```

**打包与多环境部署：**

```bash
# 打包
./mvnw clean package

# 指定环境打包
./mvnw clean package -Pprod
```

**健康检查：**

```bash
curl http://localhost:48080/actuator/health
```
