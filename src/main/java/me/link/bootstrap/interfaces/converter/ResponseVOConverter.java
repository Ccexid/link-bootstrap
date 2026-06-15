package me.link.bootstrap.interfaces.converter;

import me.link.bootstrap.application.command.TokenRefreshResult;
import me.link.bootstrap.domain.entity.MenuEntity;
import me.link.bootstrap.domain.entity.OperateLogEntity;
import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.domain.entity.TenantEntity;
import me.link.bootstrap.domain.entity.TenantPackageEntity;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.interfaces.dto.response.vo.MenuResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.OperateLogResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.OrganizationResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.RoleMenuResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.RoleResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.TenantPackageResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.TenantResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.TokenResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.UserResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.UserRoleResponseVO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;

/**
 * 接口层响应对象转换器，统一将应用层结果和领域实体转换为前端 VO。
 */
@Mapper(config = BaseConverter.class)
public interface ResponseVOConverter {

    MenuResponseVO toResponse(MenuEntity menu);

    OperateLogResponseVO toResponse(OperateLogEntity operateLog);

    OrganizationResponseVO toResponse(OrganizationEntity organization);

    RoleMenuResponseVO toResponse(RoleMenuEntity roleMenu);

    RoleResponseVO toResponse(RoleEntity role);

    TenantPackageResponseVO toResponse(TenantPackageEntity tenantPackage);

    TenantResponseVO toResponse(TenantEntity tenant);

    TokenResponseVO toResponse(TokenRefreshResult result);

    UserResponseVO toResponse(UserEntity user);

    UserRoleResponseVO toResponse(UserRoleEntity userRole);
}
