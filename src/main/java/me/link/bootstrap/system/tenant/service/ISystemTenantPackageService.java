package me.link.bootstrap.system.tenant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.core.common.SelectOptions;
import me.link.bootstrap.system.tenant.entity.SystemTenantPackage;

import java.util.List;

public interface ISystemTenantPackageService extends IService<SystemTenantPackage> {

    /**
     * 分页查询套餐
     */
    IPage<SystemTenantPackage> getPackagePage(Integer pageNo, Integer pageSize, String name);

    /**
     * 获取所有启用的套餐（用于下拉选择）
     */
    List<SystemTenantPackage> getValidPackageList();


    /**
     * 获取精简的下拉列表（只包含 id 和 name）
     */
    List<SelectOptions> getPackageSelect();
}