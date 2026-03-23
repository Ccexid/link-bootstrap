package me.link.bootstrap.system.tenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.core.common.SelectOptions;
import me.link.bootstrap.system.tenant.entity.SystemTenantPackage;
import me.link.bootstrap.system.tenant.mapper.SystemTenantPackageMapper;
import me.link.bootstrap.system.tenant.service.ISystemTenantPackageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户套餐服务实现类
 */
@Service
public class SystemTenantPackageServiceImpl extends ServiceImpl<SystemTenantPackageMapper, SystemTenantPackage> implements ISystemTenantPackageService {

    /**
     * 分页查询租户套餐列表
     *
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @param name     套餐名称（模糊匹配）
     * @return 分页结果
     */
    @Override
    public IPage<SystemTenantPackage> getPackagePage(Integer pageNo, Integer pageSize, String name) {
        Page<SystemTenantPackage> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SystemTenantPackage> wrapper = new LambdaQueryWrapper<SystemTenantPackage>()
                .like(StringUtils.hasText(name), SystemTenantPackage::getPackageName, name)
                .orderByDesc(SystemTenantPackage::getId);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 获取所有有效的套餐列表（状态为 0）
     *
     * @return 有效套餐列表
     */
    @Override
    public List<SystemTenantPackage> getValidPackageList() {
        return list(new LambdaQueryWrapper<SystemTenantPackage>()
                .eq(SystemTenantPackage::getStatus, 0));
    }

    /**
     * 获取精简的下拉列表（只包含 id 和 name）
     */
    @Override
    public List<SelectOptions> getPackageSelect() {
        // 只查询 ID 和名称，减少网络传输负载
        return list(new LambdaQueryWrapper<SystemTenantPackage>()
                .eq(SystemTenantPackage::getStatus, 0) // 只查询启用状态
                .select(SystemTenantPackage::getId, SystemTenantPackage::getPackageName))
                .stream().map(pkg -> SelectOptions.builder().id(pkg.getId()).name(pkg.getPackageName()).build()).collect(Collectors.toList());
    }
}