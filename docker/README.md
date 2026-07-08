# Local Middleware Images

These Docker files match `src/main/resources/application-dev.yml`.

## MySQL

- Host port: `3306`
- Database: `link-pro`
- Root password: `admin3306`
- Init scripts:
  - `src/sql/mysql/link-DDL-v0.1.sql`
  - `src/sql/mysql/link-DML-v0.1.sql`

Build from the repository root:

```bash
docker build -f docker/mysql/Dockerfile -t link-bootstrap-mysql .
docker run --name link-bootstrap-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=admin3306 -e MYSQL_DATABASE=link-pro -d link-bootstrap-mysql
```

## Redis

- Host port: `6379`
- Password: none
- Database used by the app: `0`

Build from the repository root:

```bash
docker build -f docker/redis/Dockerfile -t link-bootstrap-redis .
docker run --name link-bootstrap-redis -p 6379:6379 -d link-bootstrap-redis
```

## rnacos

- Host ports: `8848`, `9848`, `10848`
- Image: `qingpan/rnacos:stable`
- Container name: `mynacos`
- App default Nacos address: `127.0.0.1:8848`
- Default config Data ID: `top-link-mall-dev.yml`
- Default config group: `DEFAULT_GROUP`

Build from the repository root:

```bash
docker build -f docker/rnacos/Dockerfile -t link-bootstrap-rnacos .
docker run -d --name mynacos -p 8848:8848 -p 9848:9848 -p 10848:10848 link-bootstrap-rnacos
```

The app imports Nacos config with `optional:nacos:`. If the Data ID does not exist,
the existing local `application-dev.yml` values are still used.

Create this config in rnacos when you want Nacos to override local values:

```yaml
# Data ID: top-link-mall-dev.yml
# Group: DEFAULT_GROUP
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/link-pro?useSSL=true&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
    username: root
    password: admin3306
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0
```

Useful runtime overrides:

```bash
NACOS_SERVER_ADDR=127.0.0.1:8848
NACOS_NAMESPACE=
NACOS_CONFIG_GROUP=DEFAULT_GROUP
NACOS_DISCOVERY_GROUP=DEFAULT_GROUP
NACOS_CONFIG_ENABLED=true
NACOS_DISCOVERY_ENABLED=true
NACOS_DISCOVERY_REGISTER_ENABLED=true
```

## Compose

```bash
docker compose -f docker/docker-compose.yml up -d --build
```
