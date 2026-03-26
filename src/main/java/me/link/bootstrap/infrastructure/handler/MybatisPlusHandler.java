package me.link.bootstrap.infrastructure.handler; // 推荐目录

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.context.TenantContextHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MyBatis Plus 自动填充处理器
 * 适配多租户与 Sa-Token 权限体系，确保审计字段完整性
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
        LocalDateTime now = LocalDateTime.now();
        String operator = getCurrentOperator();

        // 1. 时间填充：如果手动设置了值则不覆盖，没设则自动填
        this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, UPDATE_TIME, LocalDateTime.class, now);

        // 2. 审计人填充
        this.strictInsertFill(metaObject, CREATE_BY, String.class, operator);
        this.strictInsertFill(metaObject, UPDATE_BY, String.class, operator);

        // 3. 基础字段初始化
        this.strictInsertFill(metaObject, DELETED, Integer.class, 0);
        this.strictInsertFill(metaObject, VERSION, Integer.class, 1);

        // 4. 租户 ID 填充
        fillTenantId(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间：通常强制更新为当前时刻
        this.setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);

        // 更新人：填充当前操作者
        this.strictUpdateFill(metaObject, UPDATE_BY, String.class, getCurrentOperator());
    }

    private void fillTenantId(MetaObject metaObject) {
        if (metaObject.hasSetter(TENANT_ID)) {
            // 优先检查对象是否已经手动设置了 tenantId（例如：超级管理员跨租户操作数据）
            Object existingTenantId = this.getFieldValByName(TENANT_ID, metaObject);
            if (Objects.isNull(existingTenantId)) {
                String tenantIdStr = TenantContextHolder.getTenantId();
                if (StringUtils.hasText(tenantIdStr)) {
                    try {
                        this.strictInsertFill(metaObject, TENANT_ID, String.class, tenantIdStr);
                    } catch (NumberFormatException e) {
                        log.error("租户解析失败，无效的 ID 格式: {}", tenantIdStr);
                    }
                }
            }
        }
    }

    private String getCurrentOperator() {
        try {
            // 优化：先判断当前是否在 Web 上下文中，避免 StpUtil 直接报错
            if (SaHolder.getStorage() != null && StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsString();
            }
        } catch (Exception e) {
            // 异步线程或非 Web 环境下静默处理
            log.trace("无法获取当前操作人，回退至系统默认标识");
        }
        return "SYSTEM";
    }
}