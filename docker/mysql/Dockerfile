FROM mysql:8.4

ENV TZ=Asia/Shanghai \
    MYSQL_DATABASE=link-pro \
    MYSQL_ROOT_PASSWORD=admin3306

COPY docker/mysql/conf.d/link-bootstrap.cnf /etc/mysql/conf.d/link-bootstrap.cnf
COPY src/sql/mysql/link-DDL-v0.1.sql /docker-entrypoint-initdb.d/01-link-DDL-v0.1.sql
COPY src/sql/mysql/link-DML-v0.1.sql /docker-entrypoint-initdb.d/02-link-DML-v0.1.sql

EXPOSE 3306
