package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.application.command.CreateOperateLogCommand;
import me.link.bootstrap.application.command.OperateLogPageQuery;
import me.link.bootstrap.application.command.UpdateOperateLogCommand;
import me.link.bootstrap.domain.entity.OperateLogEntity;
import me.link.bootstrap.domain.factory.OperateLogFactory;
import me.link.bootstrap.domain.repository.OperateLogRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperateLogApplicationService {

    private final OperateLogRepository operateLogRepository;

    @Transactional
    public OperateLogEntity create(CreateOperateLogCommand command) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        OperateLogEntity operateLog = OperateLogFactory.create(command.traceId(), command.userId(), command.userType(), command.userIp(), command.userAgent(), command.module(), command.operation(), command.bizId(), command.action(), command.extra(), command.success(), command.requestMethod(), command.requestUrl(), command.duration(), tenantId);
        return operateLogRepository.save(operateLog);
    }

    public OperateLogEntity get(Long id) {
        return ApplicationAssert.requireFound(operateLogRepository.findById(id), ErrorCode.OPERATE_LOG_NOT_FOUND);
    }

    public PageResult<OperateLogEntity> page(OperateLogPageQuery query) {
        return operateLogRepository.page(query.pageNo(), query.pageSize(), query.traceId(), query.userId(), query.module(), query.operation(), query.bizId(), query.success(), null, query.sortingFields());
    }

    @Transactional
    public OperateLogEntity update(UpdateOperateLogCommand command) {
        OperateLogEntity operateLog = get(command.id());
        Long tenantId = SecurityHelper.getRequiredTenantId();
        OperateLogFactory.changeBasicInfo(operateLog, command.traceId(), command.userId(), command.userType(), command.userIp(), command.userAgent(), command.module(), command.operation(), command.bizId(), command.action(), command.extra(), command.success(), command.requestMethod(), command.requestUrl(), command.duration(), tenantId);
        ApplicationAssert.requireSuccess(operateLogRepository.update(operateLog), ErrorCode.OPERATE_LOG_NOT_FOUND);
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(operateLogRepository.deleteById(id), ErrorCode.OPERATE_LOG_NOT_FOUND);
    }
}
