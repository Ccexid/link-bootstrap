FROM redis:7.4-alpine

ENV TZ=Asia/Shanghai

COPY docker/redis/redis.conf /usr/local/etc/redis/redis.conf

EXPOSE 6379

CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
