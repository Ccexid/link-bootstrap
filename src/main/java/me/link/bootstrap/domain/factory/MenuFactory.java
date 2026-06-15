package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.MenuEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 菜单领域工厂，集中封装菜单创建和变更时的业务规则校验。
 */
public final class MenuFactory {

    private MenuFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static MenuEntity create(String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        validate(name, permission, type, sort, parentId, path, icon, component, componentName, status, visible, keepAlive, alwaysShow);
        return MenuEntity.create(name, permission, type, sort, parentId, path, icon, component, componentName, status, visible, keepAlive, alwaysShow);
    }

    public static void changeBasicInfo(MenuEntity menu, String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        if (menu == null) {
            throw new IllegalArgumentException("菜单不能为空");
        }
        validate(name, permission, type, sort, parentId, path, icon, component, componentName, status, visible, keepAlive, alwaysShow);
        menu.changeBasicInfo(name, permission, type, sort, parentId, path, icon, component, componentName, status, visible, keepAlive, alwaysShow);
    }

    private static void validate(String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("菜单name不能为空");
        }
    }
}
