package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.internal.OperateLogInternalService;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogCreateRequest;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogPageRequest;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogUpdateRequest;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantIgnore;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 操作日志服务，直接编排审计日志持久化、分页过滤和租户上下文补齐。
 * <p>显式创建日志时从当前登录上下文补齐租户 ID，自动日志允许匿名请求落到平台租户。</p>
 */
@Service
@RequiredArgsConstructor
public class OperateLogApplicationService {

    private static final long PLATFORM_TENANT_ID = 0L;

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "user_id", "user_id",
            "biz_id", "biz_id",
            "tenant_id", "tenant_id"
    );

    private final OperateLogInternalService operateLogInternalService;

    @Transactional
    public OperateLogPO create(OperateLogCreateRequest request) {
        return createWithTenantId(request, SecurityHelper.getRequiredTenantId());
    }

    /**
     * 自动操作日志写入入口。
     * <p>
     * 匿名接口（如发送登录邮箱验证码）没有 Sa-Token 会话，不能强制读取租户上下文；
     * 这类日志统一归到平台租户 0，并显式忽略租户插件，避免 INSERT 阶段被补入 NULL 租户。
     * </p>
     */
    @TenantIgnore
    @Transactional
    public OperateLogPO createForCurrentContext(OperateLogCreateRequest request) {
        Long tenantId = SecurityHelper.getTenantId();
        return createWithTenantId(request, tenantId == null ? PLATFORM_TENANT_ID : tenantId);
    }

    private OperateLogPO createWithTenantId(OperateLogCreateRequest request, Long tenantId) {
        OperateLogPO operateLog = new OperateLogPO();
        applyMutableFields(operateLog, request.getTraceId(), request.getUserId(), request.getUserType(), request.getUserIp(), request.getUserAgent(), request.getModule(), request.getOperation(), request.getBizId(), request.getAction(), request.getExtra(), request.getSuccess(), request.getRequestMethod(), request.getRequestUrl(), request.getDuration());
        operateLog.setTenantId(tenantId);
        operateLogInternalService.save(operateLog);
        return operateLog;
    }

    public OperateLogPO get(Long id) {
        return ApplicationAssert.requireFound(operateLogInternalService.getById(id), ErrorCode.OPERATE_LOG_NOT_FOUND);
    }

    public PageResult<OperateLogPO> page(OperateLogPageRequest request) {
        Page<OperateLogPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<OperateLogPO> wrapper = new LambdaQueryWrapper<OperateLogPO>()
                .like(StrUtil.isNotBlank(request.getTraceId()), OperateLogPO::getTraceId, request.getTraceId())
                .eq(request.getUserId() != null, OperateLogPO::getUserId, request.getUserId())
                .like(StrUtil.isNotBlank(request.getModule()), OperateLogPO::getModule, request.getModule())
                .eq(request.getOperation() != null, OperateLogPO::getOperation, request.getOperation())
                .eq(request.getBizId() != null, OperateLogPO::getBizId, request.getBizId())
                .eq(request.getSuccess() != null, OperateLogPO::getSuccess, request.getSuccess())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), OperateLogPO::getId);
        Page<OperateLogPO> result = operateLogInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Transactional
    public OperateLogPO update(Long id, OperateLogUpdateRequest request) {
        OperateLogPO operateLog = get(id);
        applyMutableFields(operateLog, request.getTraceId(), request.getUserId(), request.getUserType(), request.getUserIp(), request.getUserAgent(), request.getModule(), request.getOperation(), request.getBizId(), request.getAction(), request.getExtra(), request.getSuccess(), request.getRequestMethod(), request.getRequestUrl(), request.getDuration());
        operateLog.setTenantId(SecurityHelper.getRequiredTenantId());
        ApplicationAssert.requireSuccess(operateLogInternalService.updateById(operateLog), ErrorCode.OPERATE_LOG_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(operateLogInternalService.removeById(id), ErrorCode.OPERATE_LOG_NOT_FOUND);
    }

    private static void applyMutableFields(OperateLogPO operateLog,
                                           String traceId,
                                           Long userId,
                                           Integer userType,
                                           String userIp,
                                           String userAgent,
                                           String module,
                                           Integer operation,
                                           Long bizId,
                                           String action,
                                           String extra,
                                           Boolean success,
                                           String requestMethod,
                                           String requestUrl,
                                           Integer duration) {
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("操作日志userId不能小于0");
        }
        if (StrUtil.isBlank(module)) {
            throw new IllegalArgumentException("操作日志module不能为空");
        }
        if (bizId == null || bizId < 0) {
            throw new IllegalArgumentException("操作日志bizId不能小于0");
        }
        if (StrUtil.isBlank(action)) {
            throw new IllegalArgumentException("操作日志action不能为空");
        }
        operateLog.setTraceId(traceId);
        operateLog.setUserId(userId);
        operateLog.setUserType(userType);
        operateLog.setUserIp(userIp);
        operateLog.setUserAgent(userAgent);
        operateLog.setModule(module);
        operateLog.setOperation(operation);
        operateLog.setBizId(bizId);
        operateLog.setAction(action);
        operateLog.setExtra(extra);
        operateLog.setSuccess(success);
        operateLog.setRequestMethod(requestMethod);
        operateLog.setRequestUrl(requestUrl);
        operateLog.setDuration(duration);
    }
}
