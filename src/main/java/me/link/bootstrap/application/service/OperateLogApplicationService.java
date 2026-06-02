package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateOperateLogCommand;
import me.link.bootstrap.application.command.OperateLogPageQuery;
import me.link.bootstrap.application.command.UpdateOperateLogCommand;
import me.link.bootstrap.domain.entity.OperateLogEntity;
import me.link.bootstrap.domain.factory.OperateLogFactory;
import me.link.bootstrap.domain.repository.OperateLogRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperateLogApplicationService {

    private final OperateLogRepository operateLogRepository;

    @Transactional
    public OperateLogEntity create(CreateOperateLogCommand command) {
        OperateLogEntity operateLog = OperateLogFactory.create(command.traceId(), command.userId(), command.userType(), command.userIp(), command.userAgent(), command.module(), command.operation(), command.bizId(), command.action(), command.extra(), command.success(), command.requestMethod(), command.requestUrl(), command.duration(), command.tenantId());
        return operateLogRepository.save(operateLog);
    }

    public OperateLogEntity get(Long id) {
        return operateLogRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPERATE_LOG_NOT_FOUND));
    }

    public PageResult<OperateLogEntity> page(OperateLogPageQuery query) {
        return operateLogRepository.page(query.pageNo(), query.pageSize(), query.traceId(), query.userId(), query.module(), query.operation(), query.bizId(), query.success(), query.tenantId(), query.sortingFields());
    }

    @Transactional
    public OperateLogEntity update(UpdateOperateLogCommand command) {
        OperateLogEntity operateLog = get(command.id());
        OperateLogFactory.changeBasicInfo(operateLog, command.traceId(), command.userId(), command.userType(), command.userIp(), command.userAgent(), command.module(), command.operation(), command.bizId(), command.action(), command.extra(), command.success(), command.requestMethod(), command.requestUrl(), command.duration(), command.tenantId());
        boolean updated = operateLogRepository.update(operateLog);
        if (!updated) {
            throw new BusinessException(ErrorCode.OPERATE_LOG_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        if (!operateLogRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.OPERATE_LOG_NOT_FOUND);
        }
    }
}
