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
- [待治理事项](#待治理事项)

## 项目定位

本项目主要承担以下职责：

1. 提供可复用的 Spring Boot 后端基础工程。
2. 以 DDD 四层结构约束业务代码边界，并提供清晰的扩展模板。
3. 沉淀认证、租户、组织、用户、角色、菜单、操作日志等平台基础业务模块。
4. 提供统一的 Web 接口响应、参数校验、分页排序、异常处理和链路追踪能力。
5. 为后续平台端、供应商端、商家端、用户端业务扩展提供基础骨架。

## 技术栈

| 分类 | 技术 | 当前版本/说明 |
|---|---|---|
| JDK | Java | 17 |
| 框架 | Spring Boot | 3.5.14 |
| Web 容器 | Undertow | 替代默认 Tomcat |
| ORM | MyBatis-Plus | 3.5.16 |
| 数据库 | H2 / MySQL | dev 默认 H2，test/prod 使用 MySQL |
| 权限框架 | Sa-Token | 1.45.0，已接入登录态、会话、权限码校验 |
| Redis | Redisson | 4.3.0 |
| Bean 映射 | MapStruct | 1.5.5.Final |
| 工具库 | Hutool / Guava / Caffeine | 通用工具、缓存、集合能力 |
| API 文档 | SpringDoc OpenAPI | Swagger UI 分组管理 |
| 链路追踪 | TraceIdFilter + MDC | 请求级 TraceId 透传 |
| 切面能力 | Spring AOP | 幂等、限流、操作日志、租户绕过 |
| 静态检查 | Qodana | `qodana.yaml` 使用 JVM linter |
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
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── TenantController.java
│   │   │   │   ├── TenantPackageController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── RoleController.java
│   │   │   │   ├── MenuController.java
│   │   │   │   ├── OrganizationController.java
│   │   │   │   ├── UserRoleController.java
│   │   │   │   ├── RoleMenuController.java
│   │   │   │   └── OperateLogController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/                    # HTTP 请求 DTO
│   │   │   │   │   ├── PageRequest.java
│   │   │   │   │   ├── SortablePageRequest.java
│   │   │   │   │   ├── auth/
│   │   │   │   │   ├── tenant/
│   │   │   │   │   ├── tenantpackage/
│   │   │   │   │   ├── user/
│   │   │   │   │   ├── role/
│   │   │   │   │   ├── menu/
│   │   │   │   │   ├── organization/
│   │   │   │   │   ├── userrole/
│   │   │   │   │   ├── rolemenu/
│   │   │   │   │   └── operatelog/
│   │   │   │   └── response/                   # HTTP 响应 DTO / VO
│   │   │   │       ├── ResultResponse.java
│   │   │   │       ├── ResultTableResponse.java
│   │   │   │       ├── LoginResponseVO.java
│   │   │   │       ├── TokenResponseVO.java
│   │   │   │       └── vo/
│   │   │   ├── converter/                      # Entity -> Response VO 转换
│   │   │   ├── validation/                     # 接口层自定义参数校验
│   │   │   │   ├── SortWhitelist.java
│   │   │   │   └── SortWhitelistValidator.java
│   │   │   └── web/
│   │   │       ├── advice/                     # Web 响应增强与全局异常处理
│   │   │       └── filter/                     # 请求解密等 Web 过滤器
│   │   │
│   │   ├── application/                        # 应用层：用例编排、事务控制、领域对象协调
│   │   │   ├── command/                        # Command / Query / Result 对象
│   │   │   ├── support/                        # 应用层断言等辅助能力
│   │   │   └── service/
│   │   │
│   │   ├── domain/                             # 领域层：业务核心，禁止依赖具体技术实现
│   │   │   ├── entity/
│   │   │   │   ├── TenantEntity.java
│   │   │   │   ├── TenantPackageEntity.java
│   │   │   │   └── XxxEntity.java
│   │   │   ├── factory/
│   │   │   │   ├── TenantFactory.java
│   │   │   │   └── XxxFactory.java
│   │   │   ├── repository/                     # 仓储接口，仅声明领域需要的能力
│   │   │   │   ├── TenantRepository.java
│   │   │   │   └── XxxRepository.java
│   │   │   └── valueobject/
│   │   │       ├── PageResult.java
│   │   │       └── StatusEnum.java
│   │   │
│   │   ├── infrastructure/                     # 基础设施层：框架配置、数据库、Redis、追踪、健康检查
│   │   │   ├── aop/                           # 幂等、限流、操作日志等切面
│   │   │   ├── config/
│   │   │   ├── crypto/                        # 接口加解密、手机号加密
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
│   │   │   ├── security/                       # 权限加载、权限缓存、登录失败限制
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
│       ├── mapper/
│       ├── schema.sql
│       ├── logback-spring.xml
│       └── spy.properties
│
└── sql/
    └── mysql/
        ├── link-DDL-v0.1.sql
        └── link-DML-v0.1.sql
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

典型应用服务：

- `TenantApplicationService`
- `TenantPackageApplicationService`

约定：

- 新增业务用例时优先在应用服务中编排。
- 应用层依赖领域层仓储接口，不依赖基础设施实现类。
- 鉴权校验优先使用注解、应用层或专门安全切面处理，不写入 Controller。

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

当前代码已按同一套 DDD 链路实现以下模块：

| 模块 | Controller | 接口路径 | 核心能力 |
|---|---|---|---|
| 认证授权 | `AuthController` | `/api/v1/auth` | 账号密码登录、邮箱验证码登录、Token 刷新、当前 Token、退出登录、公钥获取 |
| 租户管理 | `TenantController` | `/api/v1/tenant` | 创建、详情、分页、更新、删除 |
| 租户套餐 | `TenantPackageController` | `/api/v1/tenant/package` | 创建、详情、分页、更新、删除 |
| 用户管理 | `UserController` | `/api/v1/system/users` | 用户 CRUD、分页 |
| 角色管理 | `RoleController` | `/api/v1/system/role` | 角色 CRUD、分页 |
| 菜单管理 | `MenuController` | `/api/v1/system/menu` | 菜单 CRUD、分页 |
| 组织管理 | `OrganizationController` | `/api/v1/system/organization` | 组织 CRUD、分页 |
| 用户角色 | `UserRoleController` | `/api/v1/system/user-role` | 用户角色分配、CRUD、分页 |
| 角色菜单 | `RoleMenuController` | `/api/v1/system/role-menu` | 角色菜单授权、CRUD、分页 |
| 操作日志 | `OperateLogController` | `/api/v1/system/operate-log` | 操作日志 CRUD、分页 |

标准调用链路必须保持一致：

```text
XxxController
  -> XxxApplicationService
    -> XxxFactory / XxxEntity
    -> XxxRepository
      -> XxxRepositoryImpl
        -> XxxInternalService
          -> XxxMapper
            -> system_xxx
```

例外规则：认证、加解密、限流、幂等等横切能力可以在 `infrastructure/security`、`infrastructure/crypto`、`infrastructure/aop` 或 `interfaces/web` 中实现，但不得把业务编排绕回 Controller 或 Mapper。

## 代码规范

本节是当前项目的硬约束。新增代码优先复用现有 Tenant / User / Role 等模块的写法；如果确实需要例外，必须在类注释或方法注释中说明原因。

### 包结构规范

新增业务模块必须按以下结构补齐，除非该模块不是完整 CRUD 聚合：

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

分层边界：

- `interfaces` 只处理协议、参数校验、响应组装和接口文档。
- `application` 只编排用例、事务、权限所需的领域协作。
- `domain` 只表达业务模型、业务规则、仓储抽象和值对象。
- `infrastructure` 只实现技术细节、框架配置、持久化、缓存、加解密、安全适配。
- `shared/kernel` 只存放跨模块通用能力，禁止放具体业务模块语义。

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

命名收紧：

- 创建命令使用 `CreateXxxCommand`，更新命令使用 `UpdateXxxCommand`，分页查询使用 `XxxPageQuery`。
- HTTP 入参必须以 `Request` 结尾，HTTP 出参必须以 `ResponseVO` 结尾。
- 数据库对象必须以 `PO` 结尾，领域对象必须以 `Entity` 结尾。
- 枚举命名使用业务名加 `Enum` 或明确业务值对象名，例如 `StatusEnum`。
- 常量类按职责放置，API、Trace 等全局常量放 `GlobalConstants`，安全会话常量放 `SecurityConstants`。

### 注释规范

- 类、接口、枚举、record 需要说明职责。
- 复杂业务方法需要说明业务语义。
- 简单 getter/setter、显而易见的代码不需要逐行注释。
- 注释应解释“为什么”和“业务含义”，不要重复描述语法。
- 禁止提交模板化、空洞注释，例如“创建业务对象”“根据主键查询业务对象详情”；如果语义显而易见，可删除。
- 对框架自动配置、租户绕过、权限放行、加密开关、限流窗口等容易误改的逻辑，必须说明原因和影响范围。

### 参数校验规范

- HTTP 入参基础校验放在 Request DTO 中，通过 Jakarta Validation 注解实现。
- 业务不变量校验放在领域工厂或领域实体中。
- 跨对象、跨流程校验放在 Application Service 或领域服务中。
- Controller 类必须使用 `@Validated`，请求体使用 `@Valid @RequestBody`。
- 路径变量、查询参数必须按语义添加 `@NotNull`、`@NotBlank`、范围校验或自定义校验。
- 禁止在 Controller 中用 `if` 拼业务错误响应；业务错误统一抛 `BusinessException`。

示例：

```text
TenantCreateRequest 使用 @NotBlank / @NotNull 做基础入参校验
TenantFactory 负责手机号、域名、账号数量、过期时间等业务规则校验
TenantApplicationService 负责编排创建、更新、删除流程
```

### 事务规范

- 写操作在 Application Service 方法上使用 `@Transactional`。
- 查询操作默认不加事务；涉及一致性读取或多仓储组合读取时可使用 `@Transactional(readOnly = true)`。
- Controller 不处理事务。
- RepositoryImpl 不主动开启业务事务。
- 一个业务用例只能有一个清晰事务边界，优先放在 Application Service 的 public 方法上。
- 发送通知、写操作日志、刷新缓存等副作用不要混入领域实体；必要时通过应用层或 AOP/事件机制处理。

### 排序规范

项目使用 `@Sortable` + `@SortWhitelist` 实现排序白名单。

规范：

- 只有 Response VO 中标记 `@Sortable` 的字段允许前端排序。
- Controller 分页参数使用 `@SortWhitelist(XxxResponseVO.class)` 校验。
- RepositoryImpl 中维护前端字段到数据库列名的映射。
- 禁止直接把未校验的前端字段拼接进 SQL。
- 前端排序字段使用下划线命名，后端 VO 字段保持 Java 驼峰，映射由排序工具和 RepositoryImpl 统一处理。
- 新增排序字段时必须同步更新 Response VO 的 `@Sortable`、RepositoryImpl 字段映射和接口文档。

示例：

```text
前端传参：sort=-created_at,name
含义：按 created_at 降序，再按 name 升序
```

### Lombok 与 Java 规范

- 项目使用 Java 17，新增代码不得使用高于 Java 17 的语言特性。
- DTO、PO 可使用 Lombok `@Data`；Service、Controller 优先使用 `@RequiredArgsConstructor` 注入依赖。
- 领域实体允许使用 Lombok 减少样板代码，但必须保留表达业务行为的方法。
- 禁止字段注入，禁止新增 `@Autowired` 字段。
- 集合返回优先使用空集合，不返回 `null`。
- 金额、数量、ID、时间等关键字段必须使用明确类型，禁止用 `String` 临时代替。

### MapStruct 转换规范

- 接口层统一通过 `ResponseVOConverter` 做 Entity -> VO 转换。
- 持久化层统一通过 `infrastructure/persistence/converter/XxxConverter` 做 Entity <-> PO 转换。
- Converter 只做字段映射，不写业务判断、不查数据库、不调用远程服务。
- Entity -> PO 必须忽略审计字段、逻辑删除字段和框架自动填充字段。
- PO -> Entity 必须通过 `restore(...)` 还原领域对象，避免绕过领域构造约束。

### 安全与权限规范

- 全局登录态由 `SaTokenConfigure` 拦截，白名单只能放登录、公钥、Actuator、Swagger、错误页等必要入口。
- 业务写接口必须按权限码添加 `@SaCheckPermission`；权限码格式使用 `system:resource:action`。
- 创建、更新、授权等非幂等写操作必须添加或明确评估 `@Idempotent`；当前新增、更新、授权接口默认使用该注解，删除接口按幂等语义可不加。
- 高频或易被滥用接口必须添加 `@RateLimit`，限流 key 必须避免包含明文敏感信息。
- 登录成功后必须把 `tenantId`、`userType`、`isSuperAdmin` 等会话信息写入 Sa-Token Session。
- 密码只允许使用 BCrypt 等单向哈希校验，禁止明文存储、明文日志和可逆加密存储。
- 权限列表与角色列表统一通过 `PermissionCacheService` 缓存，角色、菜单、用户角色、角色菜单变更后必须同步失效相关缓存。
- 登录失败次数与账号锁定统一通过 `LoginAttemptService` 管理，不允许在 Controller 中临时实现失败计数。

### 多端访问边界规范

多端访问边界以 Sa-Token Session 中的 `tenantId`、`userType`、`isSuperAdmin` 为基础，以角色和权限码作为准入条件，以数据库租户字段和业务归属字段作为最终数据隔离。前端菜单隐藏不能作为后端授权依据。

| 访问端 | 身份与会话依据 | 数据访问边界 | 接口与权限边界 |
|---|---|---|---|
| 平台端 | 平台用户、平台角色、`isSuperAdmin` | 平台公共数据、租户管理数据；仅超级管理员或明确授权的后台任务允许跨租户查询 | `/api/v1/tenant/**`、`/api/v1/system/**` 等平台管理接口，权限码使用 `system:*:*` |
| 供应商端 | `userType`、`tenantId`、供应商侧角色 | 当前租户内的供应商业务数据；引入供应商主体后必须叠加 `supplier_id` 等归属条件 | 新增接口使用 `/api/v1/supplier/**`，权限码使用 `supplier:*:*` |
| 商家端 | `userType`、`tenantId`、商家侧角色 | 当前租户内的商家业务数据；引入门店或商家主体后必须叠加 `merchant_id`、`store_id` 等归属条件 | 新增接口使用 `/api/v1/merchant/**`，权限码使用 `merchant:*:*` |
| 用户端 | `userType`、用户 ID、用户侧角色或登录态 | 用户本人数据和有明确归属关系的订单、售后、账户等数据；租户隔离不能替代用户归属校验 | 新增接口避免使用 `system:user:*`，建议使用 `/api/v1/member/**` 与 `member:*:*` |

- `userType` 必须先沉淀为枚举或常量再参与端类型判断，禁止在业务代码中散落魔法数字。
- 平台普通用户不等同于超级管理员；只有 `isSuperAdmin = true` 或明确标注 `@TenantIgnore` 且有权限码保护的方法，才允许跨租户访问。
- `@TenantIgnore` 只允许用于平台级公共配置、超级管理员跨租户管理、登录前必要查询和后台治理任务，并必须贴在最小方法范围。
- 供应商端、商家端、用户端默认不得访问 `/api/v1/tenant/**` 和平台级 `/api/v1/system/**` 管理接口，例外必须通过独立权限码和应用层业务校验双重约束。
- `dataScope`、`dataScopeDeptIds` 只解决同一租户内的组织数据范围，不替代端边界、租户隔离和业务主体归属校验。
- 新增菜单、按钮、接口权限时必须同时标明适用访问端；同名业务动作在不同端语义不同的，不得复用同一个权限码。

### 幂等、限流与审计规范

- `@Idempotent` 只贴在会改变系统状态的接口或应用服务方法上，默认使用请求指纹；前端传幂等键时必须使用注解定义的请求头。
- 幂等键、限流键统一由切面写入 Redis / Redisson，业务代码不得手写重复提交判断。
- `@RateLimit` 的 `key` 使用 SpEL 时不得直接暴露手机号、邮箱、Token 等敏感值；确需按敏感字段限流时必须依赖切面哈希后的 Redis Key。
- 操作日志由 `OperateLogAspect` 自动覆盖 Controller 公共方法，新增 Controller 不要手动调用 `OperateLogApplicationService`。
- 操作日志接口自身必须避免递归记录；新增审计切面时要显式处理自调用或循环写入场景。
- 操作日志 extra 字段只记录必要上下文，禁止记录密码、Token、验证码、私钥和完整敏感请求体。

### 多租户规范

- 需要租户隔离的业务表必须包含 `tenant_id`，PO 继承 `TenantBaseDO` 或显式处理租户字段。
- 默认依赖 `LinkTenantLineHandler` 注入租户条件，禁止在业务 SQL 中手写绕过租户过滤。
- 只有登录前查询、平台级公共配置、超级管理员必要查询等场景允许使用 `@TenantIgnore`。
- `@TenantIgnore` 必须贴在最小范围的方法上，并在注释中说明为什么必须绕过租户隔离。
- RepositoryImpl 返回领域对象前必须确认查询结果没有跨租户泄露风险。

### 日志与链路追踪规范

- 日志使用 Lombok `@Slf4j`，禁止 `System.out.println`。
- 日志必须能通过 `traceId` 与请求关联；响应由 `TraceIdResponseBodyAdvice` 补充 `traceId`。
- 登录失败、权限拒绝、加解密失败、幂等拦截、限流拦截等安全相关事件必须记录 warn 级别日志。
- 禁止记录密码、Token、私钥、验证码、完整手机号等敏感信息。
- 业务日志只记录必要 ID、状态和结果，不记录大对象完整 JSON。

### 接口加解密规范

- 接口加解密由 `ApiCryptoRequestFilter` 和 `ApiCryptoResponseBodyAdvice` 统一处理。
- 开关、字段名、密钥、包含路径和排除路径统一放在 `link.api-crypto` 配置下。
- 开启后请求体必须使用 `{ "data": "RSA密文" }` 结构，响应体同样包裹密文 `data`。
- `/api/v1/auth/public-key`、Actuator、Swagger 必须保持排除，避免前端无法获取公钥或调试文档失效。
- 生产环境密钥必须通过环境变量或配置中心注入，禁止继续使用开发默认密钥。
- 手机号等业务敏感字段统一通过 `MobileCryptoService` 保护，数据库仅保存密文、哈希、掩码和 key version；查询条件需要使用哈希字段，响应只能返回掩码或脱敏值。
- 新增敏感字段时必须同步检查 Request DTO、Entity、PO、Converter、RepositoryImpl 和 Response VO，禁止在转换器中绕过加密策略。

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

### REST 路径规范

- 所有业务 API 必须挂在 `GlobalConstants.API_PREFIX` 下。
- 系统管理类接口统一使用 `/api/v1/system/**`。
- 认证类接口统一使用 `/api/v1/auth/**`。
- 新增资源路径默认使用单数业务名，例如 `/tenant`、`/system/role`；已有路径以 Controller 当前实现为准，例如 `/system/users`。
- 创建使用 `POST /resource`，详情使用 `GET /resource/{id}`，分页使用 `GET /resource`，更新使用 `PUT /resource/{id}`，删除使用 `DELETE /resource/{id}`。
- 授权、刷新 Token、发送验证码等非 CRUD 动作允许使用动词路径，例如 `/auth/refresh-token`、`/auth/email-code`、`/system/role-menu/authorize`。
- 认证白名单必须与 `SaTokenConfigure` 保持一致；新增登录前接口时必须同步检查接口加解密排除列表和限流策略。

### Controller 规范

- Controller 必须标注 `@RestController`、`@RequestMapping(GlobalConstants.API_PREFIX + "...")`、`@Validated`、`@RequiredArgsConstructor`。
- Swagger 注解使用 `@Tag` 和 `@Operation`，接口摘要写用户能理解的业务动作。
- Controller 只允许组装 Command / Query、调用 Application Service、转换 Response VO。
- Controller 禁止直接依赖 Mapper、InternalService、RepositoryImpl、PO。
- Controller 禁止写事务、缓存、SQL、复杂业务判断和权限数据查询。
- 返回值必须使用 `ResultResponse<T>` 或 `ResultTableResponse<T>`，不得直接返回 Entity、PO、Map 或原始集合。
- Entity -> VO 统一使用 `ResponseVOConverter`；Controller 中禁止散落私有 `toResponse(...)` 方法。
- 分页接口入参必须继承 `SortablePageRequest`，并使用 `@Validated @SortWhitelist(XxxResponseVO.class)`。

### JSON 与时间规范

- Jackson 全局使用 `SNAKE_CASE`，HTTP JSON 字段对外表现为下划线命名。
- Java 代码字段保持驼峰命名，不为迎合前端改成下划线字段。
- `Long`、`long`、`BigInteger` 会序列化为字符串，避免前端 JS 精度丢失。
- `LocalDateTime` 统一格式为 `yyyy-MM-dd HH:mm:ss`。
- 响应默认过滤 `null` 字段，接口契约中不要依赖 `null` 字段占位。

### 认证接口规范

- 登录入口、邮箱验证码、公钥接口必须在 `SaTokenConfigure` 白名单内。
- 需要登录的接口不要手动调用登录校验，默认交给全局拦截器处理。
- 需要权限码的接口使用 `@SaCheckPermission`，不要在 Controller 中手写角色判断。
- Token 统一从 `Authorization` 请求头读取，格式为 `Bearer <token>`。
- 前端获取接口加密公钥使用 `/api/v1/auth/public-key`。

## 持久化规范

### PO 规范

- PO 只表示数据库表结构。
- PO 继承 `BaseDO` 获取公共审计字段。
- 需要租户隔离的 PO 继承 `TenantBaseDO` 或显式声明 `tenantId`。
- JSON 字段使用 `JacksonTypeHandler`。
- 逻辑删除字段由 `BaseDO.deleted` 管理。
- 表名必须通过 `@TableName` 显式声明；JSON 字段需要 `autoResultMap = true`。
- 数据库关键字字段必须用反引号保护，例如 ``@TableField("`status`")``。
- PO 禁止包含业务行为方法，业务行为放在 Entity。

### Mapper 规范

- Mapper 继承 `BaseMapper<XxxPO>`。
- 简单 CRUD 优先使用 MyBatis-Plus。
- 复杂 SQL 再写入 `resources/mapper/XxxMapper.xml`。
- XML 文件必须与 Mapper 同名，放在 `src/main/resources/mapper/`。
- 手写 SQL 必须显式考虑逻辑删除、多租户、排序白名单和分页限制。
- 禁止使用未校验的 `${}` 拼接前端输入；排序字段必须通过白名单映射后再拼接。

### Converter 规范

- 使用 MapStruct。
- Entity -> PO 时忽略审计字段：`creator`、`createTime`、`updater`、`updateTime`、`deleted`。
- PO -> Entity 通过 `restore(...)` 方法还原领域对象。
- 转换器命名为 `XxxConverter`，放在 `infrastructure/persistence/converter`。
- 多字段组合、脱敏、权限过滤不放 Converter，按职责放到领域、应用或接口响应组装层。

### RepositoryImpl 规范

- RepositoryImpl 实现 domain 层仓储接口。
- RepositoryImpl 负责 QueryWrapper、Page、排序映射等持久化细节。
- RepositoryImpl 返回领域对象，不返回 PO。
- RepositoryImpl 只依赖 InternalService、Mapper、Converter 等基础设施组件，不暴露给 Controller。
- 查询不存在时按仓储接口约定返回 `null`、`Optional` 或抛业务异常，不得混用。
- 分页查询必须使用 `PageResult<T>` 返回领域分页结果。

### InternalService 规范

- `XxxInternalService` 是 MyBatis-Plus `IService` 的内部封装，只能在基础设施层使用。
- `XxxInternalServiceImpl` 只处理通用持久化能力，不写领域业务规则。
- 对外业务不得直接注入 InternalService，必须通过 domain Repository 抽象访问。

### 数据库脚本规范

- MySQL DDL / DML 放在 `src/sql/mysql/`，版本号随脚本名递增。
- 修改表结构时必须同步检查 PO、Mapper XML、Entity、Converter、Response VO、排序映射。
- 公共审计字段、逻辑删除字段、租户字段要与 `BaseDO` / `TenantBaseDO` 保持一致。
- 新增索引要围绕分页筛选条件、租户字段、逻辑删除字段和唯一性约束设计。

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
- 认证失败、无权限、参数错误、资源不存在必须使用明确错误码，不得统一抛 `SYSTEM_ERROR`。
- 异常消息必须面向用户或调用方可理解，内部诊断信息写日志。
- 新增 ErrorCode 后必须确认 HTTP 状态映射是否符合语义。

### 响应增强规范

- `traceId` 由 `TraceIdResponseBodyAdvice` 统一补充，业务代码不手动设置。
- `sortableFields` 由 `SortableFieldsResponseBodyAdvice` 统一补充，Controller 不手动维护。
- 加密响应由 `ApiCryptoResponseBodyAdvice` 统一处理，业务响应对象仍按明文统一响应结构编写。

## 配置与运行

项目采用多 Profile 配置：

| 环境 | 配置文件 | 说明 |
|---|---|---|
| dev | `application-dev.yml` | 开发环境，默认激活 |
| test | `application-test.yml` | 测试环境 |
| prod | `application-prod.yml` | 生产环境 |

更多环境说明见：`PROFILE_USAGE.md`

### 配置文件归属

- `application.yml` 只放跨环境公共配置和默认 profile，不放生产密钥。
- `application-dev.yml` 可保留本地 H2、Swagger、调试日志等开发便利配置。
- `application-test.yml`、`application-prod.yml` 必须使用外部环境变量或配置中心注入数据库、Redis、接口加密密钥、手机号加密密钥等敏感配置。
- 如果后续接入 Nacos，优先迁移环境相关和敏感配置；仓库内只保留本地开发默认值、配置项说明和安全占位符。
- 新增 `link.*` 配置项必须提供 `@ConfigurationProperties` 类型绑定，禁止在业务代码中散落 `@Value`。

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

编译必须作为提交前最低校验；涉及 MapStruct、配置属性、Mapper XML、注解处理器的修改尤其要执行。

### 测试

```bash
./mvnw test
```

当前 `pom.xml` 中配置了：

```text
maven.test.skip=true
```

因此测试命令会走 Maven 生命周期，但默认跳过测试编译与执行。

需要强制执行测试时使用：

```bash
./mvnw test -Dmaven.test.skip=false
```

新增领域规则、加解密、安全拦截、排序分页、异常处理时，必须补充或更新测试。

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

## 待治理事项

### 1. 权限模型收口

- 梳理菜单权限、按钮权限、接口权限的编码字典。
- 为超级管理员、租户管理员、普通用户建立最小权限测试用例。

### 2. 多租户隔离复核

- 逐表确认平台表、租户表、关联表边界。
- 逐个 Mapper XML 复核租户条件和逻辑删除条件。
- 为 `@TenantIgnore` 建立清单，确认每个绕过点都有注释和测试。
- 增加跨租户访问保护用例。

### 3. 菜单与套餐联动

- 将租户套餐菜单授权与角色授权串联。
- 支持套餐变更后自动影响租户可用菜单范围。
- 增加菜单树查询、按钮权限编码、接口权限编码的契约说明。

### 4. 领域事件机制

当前领域实体直接完成状态变更，尚未发布领域事件。

- 增加 `domain/event` 包。
- 定义 `TenantCreatedEvent`、`TenantDisabledEvent`、`TenantPackageChangedEvent` 等事件。
- 使用应用层发布事件，基础设施层监听并处理异步副作用。
- 将操作日志、通知、缓存刷新从主流程中解耦。

### 5. 审计与操作日志增强

- 完善 `OperateLogAspect` 覆盖范围。
- 自动记录模块、操作类型、业务 ID、traceId、用户、租户和结果状态。
- 支持敏感字段脱敏。
- 支持操作日志分页查询和审计导出。

### 6. DTO / Entity 转换继续收敛

- 继续减少 Controller 中的手写 Command / Query 组装样板。
- 评估是否增加 interfaces assembler 层。
- 将 Request -> Command、Entity -> VO 转换尽量集中管理。

### 7. 测试体系建设

当前 Maven 配置默认跳过测试。

- 打开单元测试执行。
- 为领域工厂补充规则测试。
- 为 Application Service 补充用例测试。
- 为 Controller 补充 WebMvcTest。
- 为 RepositoryImpl 补充持久化集成测试。

### 8. 缓存与性能优化

- 对租户套餐、菜单树等低频变更数据增加本地缓存或 Redis 缓存。
- 统一缓存 Key 规范。
- 增加缓存失效事件。
- 对分页查询建立必要索引和慢 SQL 监控。

### 9. 安全增强

- 生产环境禁止默认密码和默认密钥。
- 敏感配置全部改为环境变量或配置中心。
- 联系手机号等敏感字段支持加密存储和脱敏返回。
- 增加 XSS、CSRF、SQL 注入等安全检查策略。

### 10. 自动化与工程治理

- 在 Qodana 基础上评估增加 Checkstyle / Spotless / PMD。
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
codegraph sync
```
