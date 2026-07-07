package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import me.link.bootstrap.application.support.OperateLogRecord;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogPageRequest;
import me.link.bootstrap.interfaces.dto.response.vo.OperateLogResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface OperateLogService extends IService<OperateLogPO> {

    OperateLogPO createForCurrentContext(OperateLogRecord record);
    OperateLogResponseVO get(Long id);
    PageResult<OperateLogResponseVO> page(OperateLogPageRequest request);
}
