package me.link.bootstrap.infrastructure.persistence.internal;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;

/**
 * 菜单持久化内部服务，封装 MyBatis-Plus 对菜单表的基础操作能力。
 */
public interface MenuInternalService extends IService<MenuPO> {
}
