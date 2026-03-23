package me.link.bootstrap.system.application.menu.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.system.application.menu.IMenuService;
import me.link.bootstrap.system.domain.menu.entity.Menu;
import me.link.bootstrap.system.infrastructure.menu.mapper.MenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 菜单应用服务实现
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Override
    public List<Menu> getMenuTree(String tenantType) {
        // 1. 获取基础列表
        // 2. 转换为树形结构（此处假设前端需要嵌套结构，或者通过 ID/ParentID 关联）
        return list(new LambdaQueryWrapper<Menu>()
                .eq(StringUtils.hasText(tenantType), Menu::getTenantType, tenantType)
                .orderByAsc(Menu::getOrderNum));
    }
}