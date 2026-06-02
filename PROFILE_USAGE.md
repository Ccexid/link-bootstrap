# 多环境配置使用说明

## 配置文件说明

项目采用多Profile配置管理，不同环境使用不同的配置文件：

| 环境 | 配置文件 | 说明 |
|------|---------|------|
| **dev** | `application-dev.yml` | 开发环境(默认) |
| **test** | `application-test.yml` | 测试环境 |
| **prod** | `application-prod.yml` | 生产环境 |

## 使用方式

### 1. IDEA开发环境运行

默认使用dev配置，无需额外配置。

### 2. Maven命令行构建

```bash
# 开发环境(默认)
mvn clean package

# 测试环境
mvn clean package -Ptest

# 生产环境
mvn clean package -Pprod
```

### 3. Jar包启动

```bash
# 开发环境
java -jar bootstrap.jar --spring.profiles.active=dev

# 测试环境
java -jar bootstrap.jar --spring.profiles.active=test

# 生产环境(推荐配合环境变量)
DB_HOST=192.168.1.100 \
DB_PASSWORD=secure_password \
REDIS_HOST=192.168.1.200 \
REDIS_PASSWORD=redis_pass \
JWT_SECRET=your-secret-key \
java -jar bootstrap.jar --spring.profiles.active=prod
```

### 4. Docker部署

```bash
docker run -d \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=mysql-prod \
  -e DB_PASSWORD=${DB_PASSWORD} \
  -e REDIS_HOST=redis-prod \
  -e REDIS_PASSWORD=${REDIS_PASSWORD} \
  -e JWT_SECRET=${JWT_SECRET} \
  -p 48080:48080 \
  link-platform:latest
```

## 环境差异对比

### 数据库配置

| 配置项 | dev | test | prod |
|--------|-----|------|------|
| 数据库类型 | H2(文件) | MySQL | MySQL |
| 连接池大小 | 10 | 15 | 30 |
| SQL打印 | ✅ 开启 | ❌ 关闭 | ❌ 关闭 |
| 密码管理 | 明文 | 环境变量 | 环境变量 |

### Redis配置

| 配置项 | dev | test | prod |
|--------|-----|------|------|
| 地址 | 127.0.0.1 | 192.168.0.245 | 环境变量 |
| Database | 0 | 1 | 环境变量 |
| 连接池 | 8 | 12 | 32 |
| 密码 | 无 | 环境变量 | 环境变量 |

### 安全配置

| 配置项 | dev | test | prod |
|--------|-----|------|------|
| Swagger文档 | ✅ 开启 | ✅ 开启 | ❌ 关闭 |
| Sa-Token日志 | ✅ 开启 | ✅ 开启 | ❌ 关闭 |
| 健康检查详情 | always | always | when-authorized |
| Actuator端点 | 全部 | 全部 | health,info |

### 日志配置

| 配置项 | dev | test | prod |
|--------|-----|------|------|
| 应用日志级别 | DEBUG | INFO | INFO |
| 输出方式 | 控制台 | 控制台 | 文件 |
| TraceId | ✅ | ✅ | ✅ |
| 日志文件 | - | - | ./logs/link-prod.log |

## 环境变量列表(生产环境)

生产环境建议使用环境变量管理敏感信息：

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `DB_HOST` | 数据库主机 | `mysql-prod.cluster.local` |
| `DB_PORT` | 数据库端口 | `3306` |
| `DB_NAME` | 数据库名称 | `link-prod` |
| `DB_USERNAME` | 数据库用户名 | `link_app` |
| `DB_PASSWORD` | 数据库密码 | `********` |
| `REDIS_HOST` | Redis主机 | `redis-prod.cluster.local` |
| `REDIS_PORT` | Redis端口 | `6379` |
| `REDIS_DATABASE` | Redis数据库 | `0` |
| `REDIS_PASSWORD` | Redis密码 | `********` |
| `JWT_SECRET` | JWT密钥 | `your-secure-secret-key` |

## 最佳实践

1. **开发环境**: 使用H2数据库，无需安装MySQL，快速启动
2. **测试环境**: 使用真实MySQL，模拟生产环境
3. **生产环境**:
   - ✅ 所有敏感信息使用环境变量
   - ✅ 关闭Swagger文档
   - ✅ 关闭SQL打印
   - ✅ 限制Actuator端点暴露
   - ✅ 使用文件日志，配置日志轮转
   - ✅ 启用SSL连接数据库

## 配置优先级

Spring Boot配置优先级(高→低):

1. 命令行参数 `--spring.profiles.active=prod`
2. Java系统属性 `-Dspring.profiles.active=prod`
3. 环境变量 `SPRING_PROFILES_ACTIVE=prod`
4. Maven Profile `-Pprod`
5. 配置文件默认值 `dev`

## 验证配置

启动后检查日志输出，确认激活的Profile:

```text
The following 1 profile is active: "dev"
```

访问健康检查端点验证:

```bash
curl http://localhost:48080/actuator/health
```
