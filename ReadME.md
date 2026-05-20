# ReadME

## Descriptions

```text
src/
├── main/
│   ├── java/me/link/bootstrap/
│   │   ├── application/
│   │   │   ├── dto/                # CQRS 架构下的 Command/Query DTO
│   │   │   └── service/            # 应用服务，负责编排领域模型、事务控制、发送通知
│   │   ├── domain/
│   │   │   ├── aggregate/          # 聚合根
│   │   │   ├── entity/             # 领域实体（纯业务逻辑，无数据库注解）
│   │   │   ├── event/              # 领域事件（如 LinkCreatedEvent）
│   │   │   ├── factory/            # 复杂领域对象的创建工厂
│   │   │   ├── repository/         # 仓储接口（注意：这里只有接口定义！）
│   │   │   ├── service/            # 领域服务（处理跨实体的核心业务逻辑）
│   │   │   └── valueobject/        # 值对象
│   │   ├── infrastructure/
│   │   │   ├── config/             # 配置类
│   │   │   ├── messaging/          # 消息队列发送端的实现（实现应用层或领域层的投递接口）
│   │   │   ├── persistence/        # 持久化层
│   │   │   │   ├── converter/      # PO 与 Entity 之间的双向转换器
│   │   │   │   ├── po/             # 改变：将 entity 改为 po，避免与 domain 冲突
│   │   │   │   └── repository/     # 仓储接口的实现类（如 MyBatis Mapper 或 JPA Repository）
│   │   │   └── tracing/
│   │   │       ├── TraceIdContext.java
│   │   │       └── TraceIdFilter.java
│   │   ├── interfaces/             # 用户接口层（所有的输入源）
│   │   │   ├── assembler/          # DTO 与 Entity 之间的转换
│   │   │   ├── controller/         # HTTP 接口
│   │   │   │   └── DemoController.java
│   │   │   ├── mq/                 # 新增：MQ 消费者/监听器也属于输入源，应放于接口层
│   │   │   │   └── LinkEventListener.java 
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   │       └── ResultResponse.java
│   │   │   └── exception/          # 仅存放 GlobalExceptionHandler 等 Web 统一异常拦截
│   │   ├── shared/                 # 共享内核与基础公共组件
│   │   │   ├── exception/          # 改变：ErrorCode 和业务异常基类放在这里，供全层级引用
│   │   │   │   └── ErrorCode.java
│   │   │   └── component/          # 改变：通用的底层技术组件
│   │   │       └── BloomRepository.java
│   │   └── LinkMainApplication.java
│   └── resources/
│       ├── META-INF/services/
│       ├── mapper/
│       ├── application.yml
│       ├── logback-spring.xml
│       ├── schema.sql
│       └── spy.properties
```