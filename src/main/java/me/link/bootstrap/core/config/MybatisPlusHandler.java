package me.link.bootstrap.core.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.core.tenant.TenantContextHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 优化后的 MyBatis Plus 自动填充处理器
 * 适配 P2S2B2C 多租户架构与 Sa-Token 权限体系
 */
@Slf4j
@Component
public class MybatisPlusHandler implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_TIME = "updateTime";
    private static final String CREATE_BY = "createBy";
    private static final String UPDATE_BY = "updateBy";
    private static final String TENANT_ID = "tenantId";
    private static final String DELETED = "deleted";
    private static final String VERSION = "version";

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始执行 [Insert] 自动填充...");
        LocalDateTime now = LocalDateTime.now();
        String currentOperator = getCurrentOperator();

        // 1. 时间填充
        this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, UPDATE_TIME, LocalDateTime.class, now);

        // 2. 审计人填充
        this.strictInsertFill(metaObject, CREATE_BY, String.class, currentOperator);
        this.strictInsertFill(metaObject, UPDATE_BY, String.class, currentOperator);

        // 3. 逻辑删除与版本号初始值
        this.strictInsertFill(metaObject, DELETED, Integer.class, 0);
        this.strictInsertFill(metaObject, VERSION, Integer.class, 1);

        // 4. 租户 ID 填充 (核心优化)
        fillTenantId(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始执行 [Update] 自动填充...");

        // 更新时间通常需要强制填充，不受 strictUpdateFill 的 null 检查限制
        this.setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);

        // 更新人填充
        this.strictUpdateFill(metaObject, UPDATE_BY, String.class, getCurrentOperator());
    }

    /**
     * 租户 ID 填充逻辑
     */
    private void fillTenantId(MetaObject metaObject) {
        // 如果实体类没有 tenantId 字段，直接返回
        if (!metaObject.hasSetter(TENANT_ID)) {
            return;
        }

        // 获取当前上下文中的租户 ID
        String tenantIdStr = TenantContextHolder.getTenantId();
        long tenantId = 0L; // 默认系统级租户

        if (StringUtils.hasText(tenantIdStr)) {
            try {
                tenantId = Long.parseLong(tenantIdStr);
            } catch (NumberFormatException e) {
                log.warn("租户 ID 格式异常，回退至默认值: {}", tenantIdStr);
            }
        }

        // 填充
        this.strictInsertFill(metaObject, TENANT_ID, Long.class, tenantId);
    }

    /**
     * 获取当前操作人标识
     */
    private String getCurrentOperator() {
        try {
            // 适配 Sa-Token，如果未登录则返回 SYSTEM
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsString();
            }
        } catch (Exception e) {
            // 容器启动或异步任务时可能无法获取 Session
        }
        return "SYSTEM";
    }
}