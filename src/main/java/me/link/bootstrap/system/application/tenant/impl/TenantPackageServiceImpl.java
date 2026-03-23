package me.link.bootstrap.system.application.tenant.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.system.application.tenant.ITenantPackageService;
import me.link.bootstrap.system.domain.tenant.entity.TenantPackage;
import me.link.bootstrap.system.infrastructure.tenant.mapper.TenantPackageMapper;
import me.link.bootstrap.system.interfaces.tenant.vo.TenantPackagePageReqVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 租户套餐应用服务实现
 */
@Service
public class TenantPackageServiceImpl extends ServiceImpl<TenantPackageMapper, TenantPackage> implements ITenantPackageService {
    @Override
    public IPage<TenantPackage> getPackagePage(TenantPackagePageReqVO pageReqVO) {
        Page<TenantPackage> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        return baseMapper.selectPage(page, new LambdaQueryWrapper<TenantPackage>()
                .like(StringUtils.hasText(pageReqVO.getPackageName()),
                        TenantPackage::getPackageName, pageReqVO.getPackageName())
                .eq(pageReqVO.getStatus() != null,
                        TenantPackage::getStatus, pageReqVO.getStatus())
                .orderByDesc(TenantPackage::getId));
    }
}