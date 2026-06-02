package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.OperateLogEntity;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = BaseConverter.class)
public interface OperateLogConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    OperateLogPO convert(OperateLogEntity operateLogEntity);

    default OperateLogEntity reverseConvert(OperateLogPO operateLogPO) {
        if (operateLogPO == null) {
            return null;
        }
        return OperateLogEntity.restore(
                        operateLogPO.getId(),
                        operateLogPO.getTraceId(),
                        operateLogPO.getUserId(),
                        operateLogPO.getUserType(),
                        operateLogPO.getUserIp(),
                        operateLogPO.getUserAgent(),
                        operateLogPO.getModule(),
                        operateLogPO.getOperation(),
                        operateLogPO.getBizId(),
                        operateLogPO.getAction(),
                        operateLogPO.getExtra(),
                        operateLogPO.getSuccess(),
                        operateLogPO.getRequestMethod(),
                        operateLogPO.getRequestUrl(),
                        operateLogPO.getDuration(),
                        operateLogPO.getTenantId(),
                        operateLogPO.getCreateTime(),
                        operateLogPO.getUpdateTime()
        );
    }

    List<OperateLogPO> convertList(List<OperateLogEntity> sourceList);

    List<OperateLogEntity> reverseConvertList(List<OperateLogPO> targetList);
}
