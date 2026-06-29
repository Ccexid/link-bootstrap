# Link Bootstrap

`link-bootstrap` 是一个面向类虎扑社区平台的 Spring Boot 3 后端基础工程。当前项目不再把 DDD 四层结构作为默认生成模板；小体量 CRUD、社区业务模块和平台基础模块统一采用更轻的 Spring Boot 三层结构，减少 `Command`、`Entity`、`Factory`、`Repository`、`Converter` 等重复样板。

当前主运行链路已移除默认 DDD 包结构。后续新增代码必须以本文档为准，除非明确满足“领域模型升级条件”。

## 目录

- [项目定位](#项目定位)
- [社区平台改造方向](#社区平台改造方向)
- [技术栈](#技术栈)
- [当前结构](#当前结构)
- [结构约束](#结构约束)
- [代码生成约束](#代码生成约束)
- [已实现模块](#已实现模块)
- [接口规范](#接口规范)
- [持久化规范](#持久化规范)
- [安全与多租户规范](#安全与多租户规范)
- [异常与响应规范](#异常与响应规范)
- [配置与运行](#配置与运行)
- [常用命令](#常用命令)
- [迁移策略](#迁移策略)

## 项目定位

1. 提供类虎扑社区平台的后端基础工程。
2. 用轻量三层结构支撑快速开发，避免小体量代码被 DDD 样板放大。
3. 沉淀认证、租户、组织、用户、角色、菜单、操作日志等平台基础模块，为社区业务提供管理、审计和权限底座。
4. 提供统一响应、分页排序、参数校验、全局异常、链路追踪、MyBatis-Plus 持久化、接口加解密、限流、幂等和健康检查能力。
5. 为平台端、运营端、内容审核端和用户端社区业务扩展提供清晰且低样板的生成规则。

## 社区平台改造方向

系统后续以“社区内容 + 用户互动 + 运营治理”为主线演进，当前系统管理能力只作为社区平台的基础设施，不再把租户、角色、菜单等后台管理模块当成最终业务目标。

### 产品边界

目标形态参考类虎扑社区：

- 用户可以浏览板块、话题、帖子、评论和热门内容。
- 用户可以发帖、回帖、楼中楼回复、点赞、收藏、关注、举报和接收消息通知。
- 运营可以管理板块、话题、帖子、评论、举报、审核状态、推荐位、热榜和敏感词。
- 平台可以基于权限、租户、操作日志、限流、幂等、加密和审计能力治理社区内容。

不在当前阶段优先建设的能力：

- 电商交易、供应链、复杂订单、支付和履约。
- 即时通讯级别的强实时聊天。
- 复杂推荐算法平台。早期只做可解释的热度、时间、人工置顶和运营推荐。

### 模块演进顺序

社区模块按以下顺序建设，避免先做复杂抽象：

| 优先级 | 模块 | 典型对象 | 说明 |
|---|---|---|---|
| P0 | 账号与安全 | 用户、Token、验证码、登录失败锁定 | 已有基础能力，继续服务社区用户体系 |
| P0 | 社区基础分类 | 板块、频道、话题、标签 | 板块已落地，后续频道、话题、标签继续复用同一轻量链路 |
| P0 | 内容发布 | 帖子、帖子正文、附件、评论、回复 | 社区核心链路，先保证发布、列表、详情、删除 |
| P1 | 互动行为 | 点赞、收藏、关注、浏览记录 | 默认按轻量事件表或关系表实现 |
| P1 | 运营治理 | 举报、审核、屏蔽、置顶、加精、敏感词 | 需要完整操作日志和权限码 |
| P1 | 内容分发 | 最新、热门、板块流、用户主页 | 早期用数据库排序和缓存，禁止过早引入推荐服务 |
| P2 | 消息通知 | 评论通知、点赞通知、关注通知、系统通知 | 先做站内通知，再扩展推送渠道 |
| P2 | 搜索与统计 | 关键词搜索、热榜统计、用户活跃 | 搜索可先用数据库能力，量级上来后再引入专用搜索引擎 |

### 包与接口命名

社区业务默认继续使用当前轻量结构，不新增顶层 DDD 包：

```text
interfaces/controller/CommunityPostController.java
interfaces/dto/request/community/post/CommunityPostCreateRequest.java
interfaces/dto/request/community/post/CommunityPostPageRequest.java
interfaces/dto/response/vo/CommunityPostResponseVO.java
interfaces/controller/CommunityCommentController.java
interfaces/dto/request/community/comment/CommunityCommentCreateRequest.java
interfaces/dto/request/community/comment/CommunityCommentPageRequest.java
interfaces/dto/response/vo/CommunityCommentResponseVO.java

application/service/CommunityPostApplicationService.java
application/service/CommunityCommentApplicationService.java

infrastructure/persistence/po/CommunityPostPO.java
infrastructure/persistence/mapper/CommunityPostMapper.java
infrastructure/persistence/internal/CommunityPostInternalService.java
infrastructure/persistence/internal/impl/CommunityPostInternalServiceImpl.java
infrastructure/persistence/po/CommunityCommentPO.java
infrastructure/persistence/mapper/CommunityCommentMapper.java
infrastructure/persistence/internal/CommunityCommentInternalService.java
infrastructure/persistence/internal/impl/CommunityCommentInternalServiceImpl.java
```

接口路径按端划分：

- 用户端社区接口使用 `/api/v1/community/**` 或 `/api/v1/member/**`。
- 运营/后台治理接口使用 `/api/v1/system/community/**`。
- 只读公开内容可以放在 `/api/v1/community/**`，写操作必须校验登录态和权限或用户身份。
- 管理动作使用明确动作路径，例如 `/api/v1/system/community/posts/{id}/audit`、`/api/v1/system/community/posts/{id}/pin`。

权限码按社区语义生成：

```text
community:post:create
community:post:delete
community:comment:create
system:community:audit
system:community:report:handle
```

### 社区数据建模规则

- 帖子、评论、回复、点赞、收藏、关注、举报等对象先使用 `PO + Mapper + InternalService + ApplicationService` 实现。
- 内容正文和统计字段分离；高频计数如点赞数、评论数、浏览数不得依赖每次全表 count。
- 用户行为表必须具备唯一约束，例如同一用户对同一帖子只能点赞一次。
- 删除默认优先逻辑删除；涉及审核、风控、追溯的内容不得物理删除。
- 内容状态使用明确枚举或状态字段，例如草稿、已发布、审核中、审核拒绝、已删除、已屏蔽。
- 社区内容必须保留作者 ID、租户 ID 或业务归属字段，后台跨租户治理必须显式说明原因。
- 敏感内容、举报、审核备注和操作记录禁止返回给普通用户端。
- 附件、图片、视频只在业务表保存资源标识和元信息，文件存储由独立基础设施能力承接。

### 社区领域模型升级条件

社区模块也默认不生成 DDD 结构。只有出现以下情况，才允许把单个模块升级为领域模型：

- 帖子、评论、审核状态存在多步状态机，并且状态迁移规则在多个用例中复用。
- 内容发布同时影响积分、等级、通知、风控、热榜等多个一致性边界，需要明确聚合行为。
- 互动行为需要复杂幂等、撤销、补偿和事件发布，已经无法通过 ApplicationService 私有方法清晰表达。

升级时只升级具体复杂模块，不允许为所有社区模块批量生成 `Entity`、`Factory`、`Repository`。

## 技术栈

| 分类 | 技术 | 当前版本/说明 |
|---|---|---|
| JDK | Java | 17 |
| 框架 | Spring Boot | 3.5.14 |
| Web 容器 | Undertow | 替代默认 Tomcat |
| ORM | MyBatis-Plus | 3.5.16 |
| 数据库 | MySQL | 结构和种子数据以 `src/sql/mysql/link-DDL-v0.1.sql`、`src/sql/mysql/link-DML-v0.1.sql` 为准 |
| 权限框架 | Sa-Token | 1.45.0 |
| Redis | Redisson | 4.3.0 |
| Bean 映射 | MapStruct | 仅用于必要响应/对象转换 |
| 工具库 | Hutool / Guava / Caffeine | 通用工具、缓存、集合能力 |
| API 文档 | SpringDoc OpenAPI | Swagger UI 分组管理 |
| 链路追踪 | TraceIdFilter + MDC | 请求级 TraceId 透传 |
| 切面能力 | Spring AOP | 幂等、限流、操作日志、租户绕过 |
| 构建工具 | Maven Wrapper | 推荐使用 `./mvnw` |

## 当前结构

```text
src/
├── main/
│   ├── java/me/link/bootstrap/
│   │   ├── LinkMainApplication.java
│   │   ├── interfaces/                         # Web 接口、HTTP DTO、响应 VO、Web advice/filter
│   │   │   ├── controller/
│   │   │   ├── dto/request/
│   │   │   ├── dto/response/
│   │   │   ├── converter/                      # 必要的 PO/结果 -> ResponseVO 转换
│   │   │   ├── validation/
│   │   │   └── web/
│   │   ├── application/                        # 业务服务、事务边界、用例编排
│   │   │   ├── service/
│   │   │   └── support/
│   │   ├── infrastructure/                     # 配置、持久化、Redis、安全、加解密、健康检查
│   │   │   ├── aop/
│   │   │   ├── config/
│   │   │   ├── crypto/
│   │   │   ├── health/
│   │   │   ├── persistence/
│   │   │   │   ├── internal/
│   │   │   │   ├── internal/impl/
│   │   │   │   ├── mapper/
│   │   │   │   ├── po/
│   │   │   │   └── support/                    # 分页排序等持久化辅助
│   │   │   ├── security/
│   │   │   └── tracing/
│   │   └── shared/kernel/                      # 跨模块共享技术内核和值对象
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-test.yml
│       ├── application-prod.yml
│       ├── mapper/
│       └── logback-spring.xml
└── sql/mysql/
    ├── link-DDL-v0.1.sql                  # MySQL 表结构唯一维护入口
    └── link-DML-v0.1.sql                  # MySQL 菜单、权限和种子数据唯一维护入口
```

默认调用链路：

```text
XxxController
  -> XxxApplicationService
    -> XxxInternalService / XxxMapper
      -> XxxPO
```

当前轻量样板统一以 `XxxController -> XxxApplicationService -> XxxInternalService -> XxxMapper -> XxxPO` 为准，优先参考下方“已迁移样板”模块。

当前主运行链路已经移除存量 DDD 适配器和默认 `domain` 包；新增模块不得再复制旧 DDD 样板。

## 结构约束

### 默认分层

#### 1. interfaces

职责：

- 接收 HTTP 请求。
- 执行 Jakarta Validation 基础参数校验。
- 调用应用服务。
- 使用统一响应对象返回前端。
- 将应用服务结果转换为 Response VO。

禁止：

- 直接注入 `Mapper`、`XxxInternalService`、`RepositoryImpl`。
- 写事务、SQL、缓存、复杂业务判断。
- 返回 `PO`、`Map`、原始集合或未包装对象。

#### 2. application

职责：

- 承载业务用例。
- 控制事务边界。
- 直接编排 `XxxInternalService`、`Mapper`、安全组件、加解密组件和缓存组件。
- 对小型 CRUD 执行字段规范化、业务校验、分页查询和写入逻辑。

约束：

- 写操作使用 `@Transactional`。
- 查询操作默认不加事务；复杂一致性读取可使用 `@Transactional(readOnly = true)`。
- `ApplicationService` 可以直接接收 Request DTO，避免为透传参数生成 Command/Query。
- 登录、验证码、Token 等平台用例同样遵循该规则；除非存在明确的跨接口内部语义，否则不得为 Request DTO 再包一层 Command。
- `ApplicationService` 可以返回 `PO` 或 `PageResult<PO>`，Controller 必须转换为 Response VO。
- 不得在服务中拼接未校验 SQL 排序字段。

#### 3. infrastructure

职责：

- 框架配置、数据库访问、Redis、安全、加解密、健康检查、链路追踪。
- `PO`、`Mapper`、`InternalService` 归属持久化实现。
- 复杂 SQL 放入 `resources/mapper`。

约束：

- `XxxInternalService` 只封装 MyBatis-Plus `IService<XxxPO>`。
- `XxxInternalServiceImpl` 只继承 `ServiceImpl<XxxMapper, XxxPO>`，不写业务规则。
- 简单 CRUD 不再额外生成 `domain.repository.XxxRepository` 和 `infrastructure.persistence.repository.XxxRepositoryImpl`。

#### 4. shared/kernel

职责：

- 存放跨模块通用技术能力。
- 包括统一异常、统一常量、通用状态枚举、分页结果、排序字段、基础 DO、租户上下文、注解和工具类。

禁止：

- 放具体业务模块语义。
- 依赖 `interfaces`、`application`、`infrastructure` 中的具体业务实现类。

### 领域模型升级条件

新增模块默认不生成 `domain` 包。只有同时满足以下任意两项，才允许升级为独立领域模型：

- 存在明显聚合根，且有多个实体/值对象共同维护不变量。
- 同一对象有多种状态流转，状态变化需要封装行为方法。
- 业务规则复杂到无法通过 Request 校验和 ApplicationService 私有方法表达。
- 存在跨持久化技术或跨外部系统的仓储抽象需求。
- 规则需要在多个用例中复用，复制到服务私有方法会造成真实重复。

升级后必须在类注释中说明为什么需要 `Entity`、`Factory`、`Repository` 抽象，并在本 README 的“已迁移样板”或模块说明中记录原因。未说明原因的 DDD 样板视为结构膨胀。

## 代码生成约束

### 新增小型 CRUD 模块必须生成

以新增社区帖子 `CommunityPost` 为例：

```text
interfaces/controller/CommunityPostController.java
interfaces/dto/request/community/post/CommunityPostCreateRequest.java
interfaces/dto/request/community/post/CommunityPostUpdateRequest.java
interfaces/dto/request/community/post/CommunityPostPageRequest.java
interfaces/dto/response/vo/CommunityPostResponseVO.java

application/service/CommunityPostApplicationService.java

infrastructure/persistence/po/CommunityPostPO.java
infrastructure/persistence/mapper/CommunityPostMapper.java
infrastructure/persistence/internal/CommunityPostInternalService.java
infrastructure/persistence/internal/impl/CommunityPostInternalServiceImpl.java
resources/mapper/CommunityPostMapper.xml               # 仅复杂 SQL 需要
```

### 新增小型 CRUD 模块禁止生成

默认禁止生成以下文件：

```text
application/command/CreateXxxCommand.java
application/command/UpdateXxxCommand.java
application/command/XxxPageQuery.java
domain/entity/XxxEntity.java
domain/factory/XxxFactory.java
domain/repository/XxxRepository.java
infrastructure/persistence/converter/XxxConverter.java
infrastructure/persistence/repository/XxxRepositoryImpl.java
```

例外：满足“领域模型升级条件”并写明原因后，可以生成必要的领域文件；不能为了模板完整性补齐整套 DDD 文件。

### 生成顺序

1. 先建表结构和 `XxxPO`。
2. 建 `XxxMapper`、`XxxInternalService`、`XxxInternalServiceImpl`。
3. 建 Request DTO 和 Response VO。
4. 建 `XxxApplicationService`，在服务内完成事务、校验、分页、排序映射、敏感字段处理。
5. 建 `XxxController`，只处理 HTTP 注解、权限注解、校验和响应包装。
6. 如需排序，给 Response VO 字段加 `@Sortable`，服务中维护前端字段到数据库列名的映射。
7. 执行 `./mvnw -q -DskipTests compile`。

### 命名规范

| 类型 | 命名示例 | 说明 |
|---|---|---|
| Controller | `TenantController` | HTTP 入口 |
| Request DTO | `TenantCreateRequest` | HTTP 入参 |
| Response VO | `TenantResponseVO` | HTTP 出参 |
| Application Service | `TenantApplicationService` | 用例编排和事务边界 |
| PO | `TenantPO` | 数据库持久化对象 |
| Mapper | `TenantMapper` | MyBatis-Plus Mapper |
| InternalService | `TenantInternalService` | MyBatis-Plus `IService` 封装 |
| InternalServiceImpl | `TenantInternalServiceImpl` | `ServiceImpl` 实现 |

收紧规则：

- HTTP 入参必须以 `Request` 结尾。
- HTTP 出参必须以 `ResponseVO` 结尾。
- 数据库对象必须以 `PO` 结尾。
- 小型 CRUD 不再使用 `Command`、`Query`、`Entity`、`Factory`、`RepositoryImpl` 作为默认命名。
- 常量按职责放置，API、Trace 等全局常量放 `GlobalConstants`，安全会话常量放 `SecurityConstants`。

### 注释规范

- 类、接口、枚举、record 需要说明职责。
- 注释解释业务含义、边界或误改风险，不重复描述语法。
- 禁止模板化空洞注释，例如“创建业务对象”“根据主键查询业务对象详情”。
- 对租户绕过、权限放行、接口加解密、手机号保护、限流窗口、缓存失效等容易误改的逻辑，必须说明原因和影响范围。

## 已实现模块

| 模块 | Controller | 接口路径 | 当前结构 |
|---|---|---|---|
| 认证授权 | `AuthController` | `/api/v1/auth` | 已迁移轻量结构 + 安全组件 |
| 租户管理 | `TenantController` | `/api/v1/tenant` | 已迁移轻量结构 |
| 租户套餐 | `TenantPackageController` | `/api/v1/tenant/package` | 已迁移轻量结构 |
| 用户管理 | `UserController` | `/api/v1/system/users` | 已迁移轻量结构 |
| 角色管理 | `RoleController` | `/api/v1/system/role` | 已迁移轻量结构 |
| 菜单管理 | `MenuController` | `/api/v1/system/menu` | 已迁移轻量结构 |
| 组织管理 | `OrganizationController` | `/api/v1/system/organization` | 已迁移轻量结构 |
| 用户角色 | `UserRoleController` | `/api/v1/system/user-role` | 已迁移轻量结构 |
| 角色菜单 | `RoleMenuController` | `/api/v1/system/role-menu` | 已迁移轻量结构 |
| 操作日志 | `OperateLogController` | `/api/v1/system/operate-log` | 已迁移轻量结构 |
| 社区板块 | `CommunitySectionController` | `/api/v1/system/community/sections` | 已实现轻量结构 |
| 社区话题 | `CommunityTopicController` | `/api/v1/system/community/topics` | 已实现轻量结构 |
| 社区帖子 | `CommunityPostController` | `/api/v1/community/posts` | 已实现轻量结构 |
| 社区评论 | `CommunityCommentController` | `/api/v1/community/comments` | 已实现轻量结构 |

## 接口规范

### API 前缀

统一 API 前缀定义在 `GlobalConstants.API_PREFIX`：

```text
/api/v1
```

### REST 路径

- 所有业务 API 必须挂在 `GlobalConstants.API_PREFIX` 下。
- 系统管理类接口统一使用 `/api/v1/system/**`。
- 认证类接口统一使用 `/api/v1/auth/**`。
- 社区用户端接口统一使用 `/api/v1/community/**` 或 `/api/v1/member/**`。
- 社区运营治理接口统一使用 `/api/v1/system/community/**`。
- 新增资源路径默认使用单数业务名，例如 `/tenant`、`/system/role`。
- 创建使用 `POST /resource`。
- 详情使用 `GET /resource/{id}`。
- 分页使用 `GET /resource`。
- 更新使用 `PUT /resource/{id}`。
- 删除使用 `DELETE /resource/{id}`。
- 认证接口按资源建模:账号密码登录使用 `POST /auth/tokens`,邮箱验证码登录使用 `POST /auth/email-code-tokens`,发送邮箱验证码使用 `POST /auth/email-verification-codes`,刷新/查询/删除当前 Token 使用 `PATCH|GET|DELETE /auth/tokens/current`,获取加密公钥使用 `GET /auth/public-keys/current`。
- 授权、审核、置顶、加精、举报处理等业务动作允许使用动词路径，例如 `/system/role-menu/authorize`、`/system/community/posts/{id}/audit`。

### Controller

- 必须标注 `@RestController`、`@RequestMapping(GlobalConstants.API_PREFIX + "...")`、`@Validated`、`@RequiredArgsConstructor`。
- Swagger 注解使用 `@Tag` 和 `@Operation`。
- 请求体使用 `@Valid @RequestBody`。
- 路径变量按语义添加 `@NotNull`、`@NotBlank` 或范围校验。
- 写接口按权限码添加 `@SaCheckPermission`。
- 创建、更新、授权等非幂等写操作添加 `@Idempotent`。
- 分页接口入参继承 `SortablePageRequest`，并使用 `@Validated @SortWhitelist(XxxResponseVO.class)`。
- 返回值只使用 `ResultResponse<T>` 或 `ResultTableResponse<T>`。
- Response VO 转换统一通过 `ResponseVOConverter` 或明确的私有转换方法；同一模块内只允许一种风格。

### JSON 与时间

- Jackson 全局使用 `SNAKE_CASE`，HTTP JSON 字段对外表现为下划线命名。
- Java 代码字段保持驼峰命名。
- `Long`、`long`、`BigInteger` 会序列化为字符串，避免前端 JS 精度丢失。
- `LocalDateTime` 统一格式为 `yyyy-MM-dd HH:mm:ss`。
- 响应默认过滤 `null` 字段，接口契约不要依赖 `null` 占位。

## 持久化规范

### PO

- PO 只表示数据库表结构。
- PO 继承 `BaseDO` 获取公共审计字段。
- 需要租户隔离的 PO 继承 `TenantBaseDO` 或显式声明 `tenantId`。
- JSON 字段使用 `JacksonTypeHandler`。
- 表名必须通过 `@TableName` 显式声明。
- JSON 字段需要 `autoResultMap = true`。
- 数据库关键字字段必须用反引号保护，例如 ``@TableField("`status`")``。
- PO 禁止包含业务行为方法。

### SQL 脚本

- 数据库结构只允许维护在 `src/sql/mysql/link-DDL-v0.1.sql`。
- 初始化菜单、权限、角色授权、租户套餐菜单数组和业务种子数据只允许维护在 `src/sql/mysql/link-DML-v0.1.sql`。
- 新增表时必须同步补齐 DDL、PO、Mapper/InternalService、Response VO、排序映射和必要业务错误码。
- 新增后台接口权限时必须同步补齐 `system_menu` 种子数据、租户套餐 `menu_ids`、相关角色的 `system_role_menu` 授权。
- 新增社区业务基础数据时必须写入 `link-DML-v0.1.sql`，不要写入 `schema.sql`、临时 SQL、Java 初始化器或测试专用脚本。
- `src/main/resources/schema.sql` 不再作为业务表结构维护入口；后续改造不得继续向该文件追加业务表。

### Mapper

- Mapper 继承 `BaseMapper<XxxPO>`。
- 简单 CRUD 优先使用 MyBatis-Plus。
- 复杂 SQL 再写入 `resources/mapper/XxxMapper.xml`。
- XML 文件必须与 Mapper 同名。
- 手写 SQL 必须显式考虑逻辑删除、多租户、排序白名单和分页限制。
- 禁止使用未校验的 `${}` 拼接前端输入。

### InternalService

- `XxxInternalService` 继承 `IService<XxxPO>`。
- `XxxInternalServiceImpl` 继承 `ServiceImpl<XxxMapper, XxxPO>`。
- InternalService 是应用服务访问持久化的默认入口。
- InternalService 不写业务校验、不做权限判断、不处理响应转换。

### 分页与排序

- 分页结果统一返回 `shared/kernel/valueobject/PageResult<T>`。
- Response VO 中只有标记 `@Sortable` 的字段允许前端排序。
- Controller 分页参数使用 `@SortWhitelist(XxxResponseVO.class)` 校验。
- ApplicationService 中维护前端字段到数据库列名的映射。
- 禁止直接把前端字段拼接进 SQL。
- 新增排序字段时必须同步更新 Response VO 的 `@Sortable`、服务内排序映射和接口文档。

示例：

```text
前端传参：sort=-created_at,name
含义：按 created_at 降序，再按 name 升序
```

### 敏感字段

- 手机号等业务敏感字段统一通过 `MobileCryptoService` 保护。
- 数据库只保存密文、哈希、掩码和 key version。
- 查询条件需要使用哈希字段。
- 响应只能返回掩码或脱敏值。
- 新增敏感字段时必须同步检查 Request DTO、PO、ApplicationService、Response VO 和查询条件。

## 安全与多租户规范

### 认证与权限

- 全局登录态由 `SaTokenConfigure` 拦截。
- 白名单只能放登录、公钥、Actuator、Swagger、错误页等必要入口。
- 业务写接口必须按权限码添加 `@SaCheckPermission`。
- 后台管理查询接口也必须按权限码添加 `@SaCheckPermission`，详情接口使用 `*:query`，分页列表接口使用 `*:list`，不得只依赖全局登录态拦截器。
- 权限码格式使用 `system:resource:action`、`system:community:resource:action`、`community:resource:action` 或 `member:resource:action`。
- 高频或易被滥用接口必须添加 `@RateLimit`。
- 登录成功后必须把 `tenantId`、`userType`、`isSuperAdmin` 写入 Sa-Token Session。
- 密码只允许使用 BCrypt 等单向哈希校验。
- 登录失败次数与账号锁定统一通过 `LoginAttemptService` 管理。
- 用户管理已经迁移为轻量结构：`UserApplicationService` 直接使用 `UserInternalService`，仍需在服务内完成密码 BCrypt 哈希、手机号加密/哈希/脱敏和邮箱规范化。
- 登录前用户解析必须调用 `UserApplicationService.findByUsernameForLogin` 或 `findByEmailForLogin`，这两个方法使用最小范围 `@TenantIgnore` 绕过租户拦截；不要把 `@TenantIgnore` 扩大到整个认证流程。
- 权限列表与角色列表统一通过 `PermissionCacheService` 缓存，角色、菜单、用户角色、角色菜单变更后必须同步失效相关缓存。
- 角色管理已经迁移为轻量结构：`RoleApplicationService` 直接使用 `RoleInternalService`，仍需保留角色编码同租户唯一校验和角色变更后的权限缓存失效。

### 多端访问边界

多端访问边界以 Sa-Token Session 中的 `tenantId`、`userType`、`isSuperAdmin` 为基础，以角色和权限码作为准入条件，以数据库租户字段和业务归属字段作为最终数据隔离。

| 访问端 | 身份与会话依据 | 数据访问边界 | 接口与权限边界 |
|---|---|---|---|
| 平台端 | 平台用户、平台角色、`isSuperAdmin` | 平台公共配置、租户管理、全局社区治理数据 | `/api/v1/tenant/**`、`/api/v1/system/**`，权限码使用 `system:*:*` |
| 运营端 | 运营角色、`tenantId`、社区治理权限 | 当前租户或授权范围内的板块、话题、帖子、评论、举报和推荐位 | `/api/v1/system/community/**`，权限码使用 `system:community:*:*` |
| 审核端 | 审核角色、数据范围、审核权限 | 待审核内容、举报工单、敏感词命中记录和处理结果 | `/api/v1/system/community/**`，权限码使用 `system:community:audit:*`、`system:community:report:*` |
| 用户端 | `userType`、用户 ID | 用户本人资料、发帖、评论、点赞、收藏、关注、通知和明确公开内容 | `/api/v1/community/**`、`/api/v1/member/**`，权限码使用 `community:*:*` 或 `member:*:*` |

- 前端菜单隐藏不能作为后端授权依据。
- 平台普通用户不等同于超级管理员。
- 用户端公开浏览接口可以匿名访问，但发帖、评论、点赞、收藏、关注、举报、消息通知等接口必须校验登录态。
- 运营端审核、屏蔽、置顶、加精、推荐等动作必须记录操作日志，并保留处理前后的关键状态。
- 只有 `isSuperAdmin = true` 或明确标注 `@TenantIgnore` 且有权限码保护的方法，才允许跨租户访问。
- `@TenantIgnore` 只允许用于平台级公共配置、超级管理员跨租户管理、登录前必要查询和后台治理任务。
- `@TenantIgnore` 必须贴在最小方法范围，并说明为什么必须绕过租户隔离。
- `dataScope`、`dataScopeDeptIds` 只解决同一租户内的组织数据范围，不替代端边界、租户隔离和业务主体归属校验。

### 幂等、限流与审计

- `@Idempotent` 只贴在会改变系统状态的接口或应用服务方法上。
- 幂等键、限流键统一由切面写入 Redis / Redisson。
- `@RateLimit` 的 key 不得直接暴露手机号、邮箱、Token 等敏感值。
- 登录、验证码等公开入口使用账号、邮箱等业务维度限流时，必须开启 `includeClientIp = true`；限流切面会对业务 key 和 IP 先摘要化，再生成 Redis key。
- 操作日志由 `OperateLogAspect` 自动覆盖 Controller 公共方法。
- 操作日志作为审计数据只允许后台查询，不对前端开放 create/update/delete 接口；需要写入日志时只能通过 `OperateLogAspect` 或应用内部专用入口。
- 操作日志 extra 字段只记录必要上下文，禁止记录密码、Token、验证码、私钥和完整敏感请求体。

### 接口加解密

- 接口加解密由 `ApiCryptoRequestFilter` 和 `ApiCryptoResponseBodyAdvice` 统一处理。
- 开关、字段名、密钥、包含路径和排除路径统一放在 `link.api-crypto` 配置下。
- 开启后请求体使用 `{ "data": "RSA密文" }` 结构。
- `/api/v1/auth/public-keys/current`、Actuator、Swagger 必须保持排除。
- 生产环境密钥必须通过环境变量或配置中心注入。

## 异常与响应规范

### 统一响应

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
  "sortable_fields": ["id", "name", "created_at", "updated_at"]
}
```

### 错误码

统一错误码定义在 `ErrorCode`。

命名示例：

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
- 应用服务层、请求过滤器、AOP 切面和安全上下文工具中的主动业务校验必须走 `BusinessException + ErrorCode`，参数错误统一使用 `ApplicationAssert.invalidParam(...)` 或 `ApplicationAssert.requireValid(...)`，禁止直接 `throw new IllegalArgumentException(...)`、`throw new IllegalStateException(...)` 或 `throw new RuntimeException(...)`。
- 参数校验异常由 `GlobalExceptionHandler` 统一处理。
- Controller 不直接拼装失败响应。
- 系统异常不向前端暴露内部堆栈细节。
- 认证失败、无权限、参数错误、资源不存在必须使用明确错误码。
- 新增 ErrorCode 后必须确认 HTTP 状态映射是否符合语义。

### 响应增强

- `traceId` 由 `TraceIdResponseBodyAdvice` 统一补充。
- `sortableFields` 由 `SortableFieldsResponseBodyAdvice` 统一补充。
- 加密响应由 `ApiCryptoResponseBodyAdvice` 统一处理。

## 配置与运行

项目采用多 Profile 配置：

| 环境 | 配置文件 | 说明 |
|---|---|---|
| dev | `application-dev.yml` | 开发环境，默认激活 |
| test | `application-test.yml` | 测试环境 |
| prod | `application-prod.yml` | 生产环境 |

更多环境说明见：`PROFILE_USAGE.md`

### 配置文件归属

- `application.yml` 只放跨环境公共配置和默认 profile。
- `application-dev.yml` 可保留本地 H2、Swagger、调试日志等开发便利配置。
- `application-test.yml`、`application-prod.yml` 必须使用外部环境变量或配置中心注入数据库、Redis、接口加密密钥、手机号加密密钥等敏感配置。
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

### Swagger

开发环境可访问：

```text
http://localhost:48080/swagger-ui/index.html
```

## 常用命令

### 编译

```bash
./mvnw -q -DskipTests compile
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

因此测试命令会走 Maven 生命周期，但默认跳过测试编译与执行。需要强制执行测试时使用：

```bash
./mvnw test -Dmaven.test.skip=false
```

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

## 迁移策略

### 已迁移样板

`Auth`、`Tenant`、`TenantPackage`、`User`、`Role`、`Menu`、`Organization`、`UserRole`、`RoleMenu`、`OperateLog`、`CommunitySection` 模块已作为轻量结构样板：

- Controller 不再组装 `CreateXxxCommand`、`UpdateXxxCommand`、`XxxPageQuery`。
- ApplicationService 直接接收 Request DTO。
- ApplicationService 直接使用 `XxxInternalService` 和 `XxxPO`。
- 租户手机号保护、域名规范化、排序映射和事务边界保留在服务层。
- 租户套餐名称/备注/菜单编号规范化保留在服务层。
- 菜单名称规范化、分页查询、排序映射和权限缓存失效保留在服务层。
- 组织名称校验、联系电话格式校验、手机号加密/哈希/脱敏和租户上下文补齐保留在服务层。
- 用户密码 BCrypt 哈希、手机号加密/哈希/脱敏、邮箱规范化和登录前最小范围 `@TenantIgnore` 查询保留在服务层。
- 角色编码同租户唯一校验、分页排序映射和角色变更后的权限缓存失效保留在服务层。
- 用户角色分配、角色菜单授权的覆盖式删除/批量插入和权限缓存失效保留在服务层。
- 认证模块直接接收登录/邮箱验证码 Request DTO，Token 刷新结果放在 `application/support`，不再保留透传 `LoginCommand`、`EmailLoginCommand`、`SendEmailCodeCommand`。
- 操作日志写入由自动审计切面复用操作日志应用服务；已登录请求使用当前租户 ID，匿名公开请求统一落到平台租户 0。
- 社区板块模块直接使用 `CommunitySectionPO` 和 `CommunitySectionInternalService`，板块编码同租户唯一、父子板块删除限制、默认排序和状态归一化保留在服务层。
- 社区话题模块直接使用 `CommunityTopicPO` 和 `CommunityTopicInternalService`，话题编码同租户唯一、所属板块必须归属当前租户、默认排序和状态归一化保留在服务层。
- 社区帖子模块直接使用 `CommunityPostPO` 和 `CommunityPostInternalService`，发帖、本人编辑删除、板块/话题租户归属校验和高频计数字段保留在服务层。
- 社区评论模块直接使用 `CommunityCommentPO` 和 `CommunityCommentInternalService`，一级评论/回复层级、本人编辑删除、帖子归属校验和帖子/父评论计数维护保留在服务层。
- 已迁移模块不再保留 `XxxCommand`、`XxxQuery`、`XxxEntity`、`XxxFactory`、`XxxRepository`、`XxxRepositoryImpl`、`XxxConverter` 作为运行链路文件。

### 旧结构迁移顺序

后续如果引入或发现旧 DDD 样板代码，按复杂度从低到高迁移：

1. 纯 CRUD 且无特殊缓存失效的模块。
2. 带租户字段但无跨模块副作用的模块。
3. 带手机号等敏感字段的模块。
4. 带权限缓存失效、授权批量写入的模块。
5. 认证、安全、登录等高风险模块已完成透传 Command 清理；后续只在确有内部语义时新增 `application/support` 结果对象。

### 迁移步骤

1. 确认 Controller、ApplicationService、RepositoryImpl 的现有行为。
2. 将 RepositoryImpl 中的 QueryWrapper、Page、排序映射移动到 ApplicationService。
3. 将 Factory 中的简单校验和规范化移动到 ApplicationService 私有方法。
4. ApplicationService 直接依赖 `XxxInternalService`。
5. Controller 改为直接传 Request DTO。
6. ResponseVOConverter 改为支持 `XxxPO -> XxxResponseVO`，或在 Controller 中使用统一私有转换方法。
7. 删除不再引用的 Command、Query、Entity、Factory、Repository、RepositoryImpl、Converter。
8. 执行 `./mvnw -q -DskipTests compile`。

### 维护建议

- 新增平台基础模块优先复制租户、用户、角色等已迁移模块的轻量链路。
- 新增社区业务模块优先按“板块/话题 → 帖子 → 评论/回复 → 点赞/收藏/关注 → 举报/审核 → 热榜/通知”的顺序推进。
- 社区模块第一版先保证清晰表结构、权限边界、状态字段、幂等和审计；不要先引入复杂推荐、事件总线、搜索集群或领域模型。
- 用户端公开查询和登录后互动要拆开接口，不要在同一个方法里混合匿名浏览、用户状态判断和写行为。
- 运营端接口必须优先考虑审核状态、操作日志、权限码、数据范围和敏感字段隐藏。
- 修改数据库表时只更新 `src/sql/mysql/link-DDL-v0.1.sql` 和 `src/sql/mysql/link-DML-v0.1.sql`，并同步检查 PO、Mapper XML、Response VO、排序映射和服务查询条件。
- 新增排序字段时同步更新 VO 的 `@Sortable` 和服务内字段映射。
- 新增业务异常时同步补充 `ErrorCode`。
- 修改代码后建议执行：

```bash
./mvnw -q -DskipTests compile
./mvnw test -Dmaven.test.skip=false
```

- 修改代码结构后建议更新项目知识图谱：

```bash
codegraph sync
```
