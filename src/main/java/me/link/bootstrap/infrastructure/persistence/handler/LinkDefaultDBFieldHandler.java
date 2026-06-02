package me.link.bootstrap.infrastructure.persistence.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

import static me.link.bootstrap.shared.kernel.constant.GlobalConstants.*;

/**
 * 数据库公共字段填充处理器，负责创建人、更新人、创建时间和更新时间的自动填充。
 */
@Slf4j
public class LinkDefaultDBFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long currentUserId = getCurrentUserId();

        this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, UPDATE_TIME, LocalDateTime.class, now);

        if (currentUserId != null) {
            this.strictInsertFill(metaObject, CREATOR, Long.class, currentUserId);
            this.strictInsertFill(metaObject, UPDATER, Long.class, currentUserId);
        }

        if (log.isDebugEnabled()) {
            log.debug("插入填充完成: createTime={}, updateTime={}, creator={}, updater={}",
                    now, now, currentUserId, currentUserId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long currentUserId = getCurrentUserId();

        this.strictUpdateFill(metaObject, UPDATE_TIME, LocalDateTime.class, now);

        if (currentUserId != null) {
            this.strictUpdateFill(metaObject, UPDATER, Long.class, currentUserId);
        }

        if (log.isDebugEnabled()) {
            log.debug("更新填充完成: updateTime={}, updater={}", now, currentUserId);
        }
    }

    private Long getCurrentUserId() {
        try {
            if (StpUtil.isLogin()) {
                Long loginId = StpUtil.getLoginIdAsLong();
                if (log.isTraceEnabled()) {
                    log.trace("获取到登录用户ID: {}", loginId);
                }
                return loginId;
            }
            return SYSTEM_USER;
        } catch (Exception e) {
            log.warn("获取当前登录用户ID失败，使用SYSTEM作为默认值。异常信息: {}", e.getMessage());
            return SYSTEM_USER;
        }
    }
}
