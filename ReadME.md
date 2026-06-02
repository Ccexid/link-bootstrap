# Link Bootstrap

`link-bootstrap` 是一个面向 S2P2B2C 平台的 Spring Boot 3 后端脚手架项目。项目以 DDD 分层思想组织核心业务代码，通过接口层、应用层、领域层、基础设施层分离业务职责，同时提供统一响应、分页排序、链路追踪、全局异常处理、MyBatis-Plus 持久化、健康检查、多环境配置等基础能力。

## 目录

- [项目定位](#项目定位)
- [技术栈](#技术栈)
- [现有代码结构图](#现有代码结构图)
- [DDD 分层说明](#ddd-分层说明)
- [已实现业务模块](#已实现业务模块)
- [代码规范](#代码规范)
- [接口规范](#接口规范)
- [持久化规范](#持久化规范)
- [异常与响应规范](#异常与响应规范)
- [配置与运行](#配置与运行)
- [常用开发命令](#常用开发命令)
- [未来改造方向](#未来改造方向)

## 项目定位

本项目当前主要承担以下职责：

1. 提供可复用的 Spring Boot 后端基础工程。
2. 以 DDD 四层结构约束业务代码边界。
3. 沉淀租户、租户套餐等平台基础业务模块。
4. 提供统一的 Web 接口响应、参数校验、分页排序、异常处理和链路追踪能力。
5. 为后续认证授权、租户隔离、菜单权限、组织用户、运营审计等模块提供基础骨架。

## 技术栈

| 分类 | 技术 | 当前版本/说明 |
|---|---|---|
| JDK | Java | 17 |
| 框架 | Spring Boot | 3.5.13 |
| Web 容器 | Undertow | 替代默认 Tomcat |
| ORM | MyBatis-Plus | 3.5.16 |
| 数据库 | H2 / MySQL | dev 默认 H2，test/prod 使用 MySQL |
| 权限框架 | Sa-Token | 1.45.0，当前业务接口尚未接入鉴权 |
| Redis | Redisson | 4.3.0 |
| Bean 映射 | MapStruct | 1.5.5.Final |
| 工具库 | Hutool / Guava / Caffeine | 通用工具、缓存、集合能力 |
| API 文档 | SpringDoc OpenAPI | Swagger UI 分组管理 |
| 链路追踪 | TraceIdFilter + MDC | 请求级 TraceId 透传 |
| 构建工具 | Maven Wrapper | 推荐使用 `./mvnw` |

## 现有代码结构图

```text
src/
├── main/
│   ├── java/me/link/bootstrap/
│   │   ├── LinkMainApplication.java
│   │   │
│   │   ├── interfaces/                         # 用户接口层：协议转换、参数校验、请求分发
│   │   │   ├── controller/                     # REST Controller
│   │   │   │   ├── TenantController.java
│   │   │   │   └── TenantPackageController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/                    # HTTP 请求 DTO
│   │   │   │   │   ├── PageRequest.java
│   │   │   │   │   ├── SortablePageRequest.java
│   │   │   │   │   ├── tenant/
│   │   │   │   │   │   ├── TenantCreateRequest.java
│   │   │   │   │   │   ├── TenantUpdateRequest.java
│   │   │   │   │   │   └── TenantPageRequest.java
│   │   │   │   │   └── tenantpackage/
│   │   │   │   │       ├── TenantPackageCreateRequest.java
│   │   │   │   │       ├── TenantPackageUpdateRequest.java
│   │   │   │   │       └── TenantPackagePageRequest.java
│   │   │   │   └── response/                   # HTTP 响应 DTO / VO
│   │   │   │       ├── ResultResponse.java
│   │   │   │       ├── ResultTableResponse.java
│   │   │   │       └── vo/
│   │   │   │           ├── TenantResponseVO.java
│   │   │   │           └── TenantPackageResponseVO.java
│   │   │   ├── validation/                     # 接口层自定义参数校验
│   │   │   │   ├── SortWhitelist.java
│   │   │   │   └── SortWhitelistValidator.java
│   │   │   └── web/advice/                     # Web 响应增强与全局异常处理
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── SortableFieldsResponseBodyAdvice.java
│   │   │       └── TraceIdResponseBodyAdvice.java
│   │   │
│   │   ├── application/                        # 应用层：用例编排、事务控制、领域对象协调
│   │   │   ├── command/                        # Command / Query 对象
│   │   │   │   ├── CreateTenantCommand.java
│   │   │   │   ├── UpdateTenantCommand.java
│   │   │   │   ├── TenantPageQuery.java
│   │   │   │   ├── CreateTenantPackageCommand.java
│   │   │   │   ├── UpdateTenantPackageCommand.java
│   │   │   │   └── TenantPackagePageQuery.java
│   │   │   └── service/
│   │   │       ├── TenantApplicationService.java
│   │   │       └── TenantPackageApplicationService.java
│   │   │
│   │   ├── domain/                             # 领域层：业务核心，禁止依赖具体技术实现
│   │   │   ├── entity/
│   │   │   │   ├── TenantEntity.java
│   │   │   │   └── TenantPackageEntity.java
│   │   │   ├── factory/
│   │   │   │   ├── TenantFactory.java
│   │   │   │   └── TenantPackageFactory.java
│   │   │   ├── repository/                     # 仓储接口，仅声明领域需要的能力
│   │   │   │   ├── TenantRepository.java
│   │   │   │   └── TenantPackageRepository.java
│   │   │   └── valueobject/
│   │   │       ├── PageResult.java
│   │   │       └── StatusEnum.java
│   │   │
│   │   ├── infrastructure/                     # 基础设施层：框架配置、数据库、Redis、追踪、健康检查
│   │   │   ├── config/
│   │   │   │   ├── LinkCorsAutoConfiguration.java
│   │   │   │   ├── LinkJacksonAutoConfiguration.java
│   │   │   │   ├── LinkMybatisAutoConfiguration.java
│   │   │   │   ├── LinkSpringDocAutoConfiguration.java
│   │   │   │   └── LinkUndertowAutoConfiguration.java
│   │   │   ├── health/
│   │   │   │   ├── DatabaseHealthIndicator.java
│   │   │   │   ├── RedisHealthIndicator.java
│   │   │   │   └── SystemHealthIndicator.java
│   │   │   ├── persistence/
│   │   │   │   ├── converter/                  # Entity <-> PO 转换器
│   │   │   │   ├── handler/                    # 数据库公共字段自动填充
│   │   │   │   ├── internal/                   # MyBatis-Plus IService 封装
│   │   │   │   ├── mapper/                     # MyBatis-Plus Mapper
│   │   │   │   ├── po/                         # Persistent Object
│   │   │   │   └── repository/                 # domain.repository 的实现
│   │   │   └── tracing/
│   │   │       ├── TraceIdContext.java
│   │   │       └── TraceIdFilter.java
│   │   │
│   │   └── shared/kernel/                      # 技术共享内核：跨业务模块复用，不放具体业务语义
│   │       ├── annotation/
│   │       ├── component/
│   │       ├── constant/
│   │       ├── converter/
│   │       ├── database/mybatis/
│   │       ├── exception/
│   │       ├── util/
│   │       └── valueobject/
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-test.yml
│       ├── application-prod.yml
│       └── mapper/
│           ├── TenantMapper.xml
│           └── TenantPackageMapper.xml
│
└── sql/
    └── mysql/
        └── link-DDL-v0.1.sql
```

## DDD 分层说明

### 1. interfaces 用户接口层

职责：

- 接收 HTTP 请求。
- 做基础参数校验。
- 将 Request DTO 转换为应用层 Command / Query。
- 调用 Application Service。
- 将领域对象转换为 Response VO。

禁止：

- 直接调用 Mapper / InternalService。
- 编写复杂业务规则。
- 直接处理数据库事务。

### 2. application 应用层

职责：

- 编排一个完整业务用例。
- 控制事务边界。
- 调用领域工厂、领域对象、仓储接口。
- 处理跨实体、跨聚合的流程协调。

当前示例：

- `TenantApplicationService`
- `TenantPackageApplicationService`

约定：

- 新增业务用例时优先在应用服务中编排。
- 应用层依赖领域层仓储接口，不依赖基础设施实现类。
- 鉴权校验未来也建议在应用层或专门的安全切面中处理，不写入 Controller。

### 3. domain 领域层

职责：

- 表达业务核心模型。
- 维护业务不变量。
- 定义仓储抽象。
- 提供领域工厂、值对象、枚举、领域服务等。

约定：

- 领域层不能依赖 Spring MVC、MyBatis-Plus、数据库 PO、HTTP DTO。
- 领域实体只暴露与业务语义相关的行为，例如 `enable()`、`disable()`、`changePackage()`。
- 复杂创建逻辑放到 Factory，例如 `TenantFactory`、`TenantPackageFactory`。
- 仓储接口只定义领域需要的能力，不暴露具体 ORM API。

### 4. infrastructure 基础设施层

职责：

- 对接数据库、Redis、第三方服务、框架配置。
- 实现领域层仓储接口。
- 完成 Entity 与 PO 的转换。
- 提供技术组件，例如链路追踪、健康检查、跨域配置。

约定：

- Mapper、PO、InternalService 只放在基础设施层。
- RepositoryImpl 是领域仓储接口到数据库访问能力的适配器。
- MapStruct Converter 只做对象转换，不承载业务规则。

### 5. shared/kernel 技术共享内核

职责：

- 存放跨业务模块复用的技术能力。
- 包括统一异常、统一常量、分页排序值对象、基础 DO、MapStruct 基础配置等。

约定：

- 不放具体业务模块语义。
- 不依赖 interfaces/application/domain 中的具体业务类。

## 已实现业务模块

### 租户管理

接口路径：

```text
/api/v1/tenant
```

能力：

- 创建租户
- 查询租户详情
- 分页查询租户
- 更新租户
- 删除租户
- 启用 / 停用租户

核心链路：

```text
TenantController
  -> TenantApplicationService
    -> TenantFactory / TenantEntity
    -> TenantRepository
      -> TenantRepositoryImpl
        -> TenantInternalService
          -> TenantMapper
            -> system_tenant
```

### 租户套餐管理

接口路径：

```text
/api/v1/tenant/package
```

能力：

- 创建租户套餐
- 查询租户套餐详情
- 分页查询租户套餐
- 更新租户套餐
- 删除租户套餐
- 启用 / 停用租户套餐

核心链路：

```text
TenantPackageController
  -> TenantPackageApplicationService
    -> TenantPackageFactory / TenantPackageEntity
    -> TenantPackageRepository
      -> TenantPackageRepositoryImpl
        -> TenantPackageInternalService
          -> TenantPackageMapper
            -> system_tenant_package
```

## 代码规范

### 包结构规范

新增业务模块建议按以下结构补齐：

```text
interfaces/controller/XxxController.java
interfaces/dto/request/xxx/XxxCreateRequest.java
interfaces/dto/request/xxx/XxxUpdateRequest.java
interfaces/dto/request/xxx/XxxPageRequest.java
interfaces/dto/response/vo/XxxResponseVO.java

application/command/CreateXxxCommand.java
application/command/UpdateXxxCommand.java
application/command/XxxPageQuery.java
application/service/XxxApplicationService.java

domain/entity/XxxEntity.java
domain/factory/XxxFactory.java
domain/repository/XxxRepository.java
domain/valueobject/XxxStatus.java

infrastructure/persistence/po/XxxPO.java
infrastructure/persistence/mapper/XxxMapper.java
infrastructure/persistence/internal/XxxInternalService.java
infrastructure/persistence/internal/impl/XxxInternalServiceImpl.java
infrastructure/persistence/converter/XxxConverter.java
infrastructure/persistence/repository/XxxRepositoryImpl.java
resources/mapper/XxxMapper.xml
```

### 命名规范

| 类型 | 命名示例 | 说明 |
|---|---|---|
| Controller | `TenantController` | 接口入口 |
| Request DTO | `TenantCreateRequest` | HTTP 入参 |
| Response VO | `TenantResponseVO` | HTTP 出参 |
| Command | `CreateTenantCommand` | 应用层创建命令 |
| Query | `TenantPageQuery` | 应用层查询条件 |
| Application Service | `TenantApplicationService` | 应用用例编排 |
| Entity | `TenantEntity` | 领域实体 |
| Factory | `TenantFactory` | 领域对象创建/变更校验 |
| Repository Interface | `TenantRepository` | 领域仓储接口 |
| Repository Impl | `TenantRepositoryImpl` | 基础设施仓储实现 |
| PO | `TenantPO` | 数据库持久化对象 |
| Mapper | `TenantMapper` | MyBatis-Plus Mapper |
| Converter | `TenantConverter` | Entity / PO 转换器 |

### 注释规范

- 类、接口、枚举、record 需要说明职责。
- 复杂业务方法需要说明业务语义。
- 简单 getter/setter、显而易见的代码不需要逐行注释。
- 注释应解释“为什么”和“业务含义”，不要重复描述语法。

### 参数校验规范

- HTTP 入参基础校验放在 Request DTO 中，通过 Jakarta Validation 注解实现。
- 业务不变量校验放在领域工厂或领域实体中。
- 跨对象、跨流程校验放在 Application Service 或领域服务中。

示例：

```text
TenantCreateRequest 使用 @NotBlank / @NotNull 做基础入参校验
TenantFactory 负责手机号、域名、账号数量、过期时间等业务规则校验
TenantApplicationService 负责编排创建、更新、删除流程
```

### 事务规范

- 写操作在 Application Service 方法上使用 `@Transactional`。
- 查询操作默认不加事务，后续可按需使用只读事务。
- Controller 不处理事务。
- RepositoryImpl 不主动开启业务事务。

### 排序规范

项目使用 `@Sortable` + `@SortWhitelist` 实现排序白名单。

规范：

- 只有 Response VO 中标记 `@Sortable` 的字段允许前端排序。
- Controller 分页参数使用 `@SortWhitelist(XxxResponseVO.class)` 校验。
- RepositoryImpl 中维护前端字段到数据库列名的映射。
- 禁止直接把未校验的前端字段拼接进 SQL。

示例：

```text
前端传参：sort=-created_at,name
含义：按 created_at 降序，再按 name 升序
```

## 接口规范

### API 前缀

统一 API 前缀定义在 `GlobalConstants.API_PREFIX`：

```text
/api/v1
```

### 统一响应

单对象响应：

```json
{
  "data": {},
  "message": "操作成功",
  "code": 0,
  "timestamp": 1710000000000,
  "traceId": "xxxx"
}
```

分页响应：

```json
{
  "records": [],
  "total": 0,
  "code": 0,
  "timestamp": 1710000000000,
  "traceId": "xxxx",
  "sortableFields": ["id", "name", "created_at", "updated_at"]
}
```

### 接口路径建议

| 业务 | 方法 | 路径 | 说明 |
|---|---|---|---|
| 租户 | `POST` | `/api/v1/tenant` | 创建租户 |
| 租户 | `GET` | `/api/v1/tenant/{id}` | 查询租户详情 |
| 租户 | `GET` | `/api/v1/tenant` | 分页查询租户 |
| 租户 | `PUT` | `/api/v1/tenant/{id}` | 更新租户 |
| 租户 | `DELETE` | `/api/v1/tenant/{id}` | 删除租户 |
| 租户套餐 | `POST` | `/api/v1/tenant/package` | 创建套餐 |
| 租户套餐 | `GET` | `/api/v1/tenant/package/{id}` | 查询套餐详情 |
| 租户套餐 | `GET` | `/api/v1/tenant/package` | 分页查询套餐 |
| 租户套餐 | `PUT` | `/api/v1/tenant/package/{id}` | 更新套餐 |
| 租户套餐 | `DELETE` | `/api/v1/tenant/package/{id}` | 删除套餐 |

## 持久化规范

### PO 规范

- PO 只表示数据库表结构。
- PO 继承 `BaseDO` 获取公共审计字段。
- JSON 字段使用 `JacksonTypeHandler`。
- 逻辑删除字段由 `BaseDO.deleted` 管理。

### Mapper 规范

- Mapper 继承 `BaseMapper<XxxPO>`。
- 简单 CRUD 优先使用 MyBatis-Plus。
- 复杂 SQL 再写入 `resources/mapper/XxxMapper.xml`。

### Converter 规范

- 使用 MapStruct。
- Entity -> PO 时忽略审计字段：`creator`、`createTime`、`updater`、`updateTime`、`deleted`。
- PO -> Entity 通过 `restore(...)` 方法还原领域对象。

### RepositoryImpl 规范

- RepositoryImpl 实现 domain 层仓储接口。
- RepositoryImpl 负责 QueryWrapper、Page、排序映射等持久化细节。
- RepositoryImpl 返回领域对象，不返回 PO。

## 异常与响应规范

### 错误码

统一错误码定义在 `ErrorCode`。

命名建议：

```text
USER_NOT_FOUND
TENANT_NOT_FOUND
TENANT_PACKAGE_NOT_FOUND
SYSTEM_ERROR
```

编码建议：

```text
400_000_xxx 参数/客户端错误
401_000_xxx 未登录
403_000_xxx 无权限
404_000_xxx 资源不存在
500_模块号_xxx 业务异常
500_999_999 系统异常
```

### 异常处理

- 业务异常使用 `BusinessException`。
- 参数校验异常由 `GlobalExceptionHandler` 统一处理。
- Controller 不直接拼装失败响应。
- 系统异常不向前端暴露内部堆栈细节。

## 配置与运行

项目采用多 Profile 配置：

| 环境 | 配置文件 | 说明 |
|---|---|---|
| dev | `application-dev.yml` | 开发环境，默认激活 |
| test | `application-test.yml` | 测试环境 |
| prod | `application-prod.yml` | 生产环境 |

更多环境说明见：`PROFILE_USAGE.md`

### 本地启动

```bash
./mvnw spring-boot:run
```

### 指定环境启动

```bash
./mvnw spring-boot:run -Ptest
```

或：

```bash
java -jar target/bootstrap.jar --spring.profiles.active=prod
```

### 健康检查

```bash
curl http://localhost:48080/actuator/health
```

### Swagger 文档

开发环境可访问：

```text
http://localhost:48080/swagger-ui/index.html
```

## 常用开发命令

### 编译

```bash
./mvnw compile
```

### 测试

```bash
./mvnw test
```

当前 `pom.xml` 中配置了：

```text
maven.test.skip=true
```

因此测试命令会走 Maven 生命周期，但默认跳过测试编译与执行。

### 打包

```bash
./mvnw clean package
```

### 多环境打包

```bash
./mvnw clean package -Pdev
./mvnw clean package -Ptest
./mvnw clean package -Pprod
```

## 新增业务模块建议流程

以新增 `Product` 模块为例：

1. 根据数据库表建立 `ProductPO`。
2. 建立 `ProductMapper` 和 `ProductInternalService`。
3. 在 domain 层建立 `ProductEntity`、`ProductFactory`、`ProductRepository`。
4. 在 infrastructure 层实现 `ProductRepositoryImpl`。
5. 建立 `ProductConverter` 负责 Entity / PO 转换。
6. 在 application 层建立 Command / Query 和 `ProductApplicationService`。
7. 在 interfaces 层建立 Request DTO、Response VO 和 `ProductController`。
8. 为分页接口补充 `@Sortable` 和 `@SortWhitelist`。
9. 执行 `./mvnw compile` 和 `./mvnw test`。
10. 更新接口文档或业务说明。

## 未来改造方向

### 1. 认证与鉴权接入

当前项目已引入 Sa-Token，但租户和租户套餐 CRUD 暂未加鉴权。

建议后续：

- 接入登录、登出、刷新 Token。
- 在应用层或安全切面中增加权限校验。
- 定义菜单权限、按钮权限、数据权限。
- 区分平台端、供应商端、商家端、用户端访问边界。

### 2. 租户隔离增强

当前已有 `TenantBaseDO`，但租户隔离尚未完整落地。

建议后续：

- 建立租户上下文 `TenantContext`。
- 接入 MyBatis-Plus 租户拦截器。
- 对需要租户隔离的业务表统一继承 `TenantBaseDO`。
- 明确平台表与租户表边界。
- 增加跨租户访问保护。

### 3. 菜单与权限模型完善

租户套餐中已有 `menu_ids` 字段，但菜单模型尚需完善。

建议后续：

- 建立菜单表、角色表、角色菜单关联表的 DDD 模型。
- 将租户套餐菜单授权与角色授权串联。
- 支持套餐变更后自动影响租户可用菜单范围。
- 增加菜单树查询、按钮权限编码、接口权限编码。

### 4. 领域事件机制

当前领域实体直接完成状态变更，尚未发布领域事件。

建议后续：

- 增加 `domain/event` 包。
- 定义 `TenantCreatedEvent`、`TenantDisabledEvent`、`TenantPackageChangedEvent` 等事件。
- 使用应用层发布事件，基础设施层监听并处理异步副作用。
- 将操作日志、通知、缓存刷新从主流程中解耦。

### 5. 审计与操作日志

DDL 中已有 `system_operate_log`，但业务操作日志尚未完整接入。

建议后续：

- 建立操作日志注解或 AOP。
- 自动记录模块、操作类型、业务 ID、traceId、用户、租户。
- 支持敏感字段脱敏。
- 支持操作日志分页查询和审计导出。

### 6. DTO / Entity 转换收敛

当前 Controller 中仍存在部分手写 `toResponse(...)` 转换。

建议后续：

- 增加 interfaces assembler 层。
- 将 Request -> Command、Entity -> VO 转换集中管理。
- 避免 Controller 过多承担对象转换职责。

### 7. 测试体系建设

当前 Maven 配置默认跳过测试。

建议后续：

- 打开单元测试执行。
- 为领域工厂补充规则测试。
- 为 Application Service 补充用例测试。
- 为 Controller 补充 WebMvcTest。
- 为 RepositoryImpl 补充持久化集成测试。

### 8. 缓存与性能优化

建议后续：

- 对租户套餐、菜单树等低频变更数据增加本地缓存或 Redis 缓存。
- 统一缓存 Key 规范。
- 增加缓存失效事件。
- 对分页查询建立必要索引和慢 SQL 监控。

### 9. 安全增强

建议后续：

- 生产环境禁止默认密码和默认密钥。
- 敏感配置全部改为环境变量或配置中心。
- 联系手机号等敏感字段支持加密存储和脱敏返回。
- 增加 XSS、CSRF、SQL 注入等安全检查策略。

### 10. 自动化与工程治理

建议后续：

- 增加 Checkstyle / Spotless / PMD 等静态检查。
- 接入 CI 流水线。
- 增加接口契约测试。
- 增加数据库迁移工具，例如 Flyway 或 Liquibase。
- 将项目结构规范沉淀为脚手架生成模板。

## 维护建议

- 新增模块时优先复制现有 Tenant / TenantPackage 的 DDD 链路结构。
- 修改数据库表时同步检查 PO、Entity、Converter、ResponseVO 是否需要调整。
- 新增排序字段时同步更新 VO 的 `@Sortable` 和 RepositoryImpl 的字段映射。
- 新增业务异常时同步补充 `ErrorCode`。
- 修改代码后建议执行：

```bash
./mvnw compile
./mvnw test
```

- 修改代码结构后建议更新项目知识图谱：

```bash
graphify update .
```
