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

Build from the repository root:

```bash
docker build -f docker/rnacos/Dockerfile -t link-bootstrap-rnacos .
docker run -d --name mynacos -p 8848:8848 -p 9848:9848 -p 10848:10848 link-bootstrap-rnacos
```

## Compose

```bash
docker compose -f docker/docker-compose.yml up -d --build
```
