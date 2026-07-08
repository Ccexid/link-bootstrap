package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import me.link.bootstrap.application.support.OperateLogRecord;
import me.link.bootstrap.interfaces.dto.request.operatelog.OperateLogPageRequest;
import me.link.bootstrap.interfaces.dto.response.vo.OperateLogResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface OperateLogService extends IService<OperateLogPO> {

    /**
     * 基于当前上下文创建操作日志。
     */
    OperateLogPO createForCurrentContext(OperateLogRecord record);
    /**
     * 查询操作日志详情。
     */
    OperateLogResponseVO get(Long id);
    /**
     * 分页查询操作日志列表。
     */
    PageResult<OperateLogResponseVO> page(OperateLogPageRequest request);
}
