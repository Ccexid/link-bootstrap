package me.link.bootstrap.infrastructure.persistence.internal.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.link.bootstrap.infrastructure.persistence.internal.MenuInternalService;
import me.link.bootstrap.infrastructure.persistence.mapper.MenuMapper;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import org.springframework.stereotype.Service;

@Service
public class MenuInternalServiceImpl extends ServiceImpl<MenuMapper, MenuPO> implements MenuInternalService {
}
