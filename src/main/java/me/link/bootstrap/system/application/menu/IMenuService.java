package me.link.bootstrap.system.application.menu;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.system.domain.menu.entity.Menu;
import java.util.List;

/**
 * 菜单应用服务接口
 */
public interface IMenuService extends IService<Menu> {
    /**
     * 获取全量菜单树
     */
    List<Menu> getMenuTree(String tenantType);
}