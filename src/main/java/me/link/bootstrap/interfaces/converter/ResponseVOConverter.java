package me.link.bootstrap.interfaces.converter;

import me.link.bootstrap.application.support.TokenRefreshResult;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityTopicPO;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.infrastructure.persistence.po.OperateLogPO;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import me.link.bootstrap.interfaces.dto.response.vo.CommunitySectionResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityPostResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityTopicResponseVO;
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
import org.mapstruct.Mapping;

/**
 * 接口层响应对象转换器，统一将应用层结果、领域实体或 PO 转换为前端 VO。
 */
@Mapper(config = BaseConverter.class)
public interface ResponseVOConverter {

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    CommunitySectionResponseVO toResponse(CommunitySectionPO section);

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    CommunityPostResponseVO toResponse(CommunityPostPO post);

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    CommunityTopicResponseVO toResponse(CommunityTopicPO topic);

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    MenuResponseVO toResponse(MenuPO menu);

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    OperateLogResponseVO toResponse(OperateLogPO operateLog);

    @Mapping(target = "contactMobile", source = "contactMobileMask")
    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    OrganizationResponseVO toResponse(OrganizationPO organization);

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    RoleMenuResponseVO toResponse(RoleMenuPO roleMenu);

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    RoleResponseVO toResponse(RolePO role);

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    TenantPackageResponseVO toResponse(TenantPackagePO tenantPackage);

    @Mapping(target = "contactMobile", source = "contactMobileMask")
    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    TenantResponseVO toResponse(TenantPO tenant);

    TokenResponseVO toResponse(TokenRefreshResult result);

    @Mapping(target = "mobile", source = "mobileMask")
    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    UserResponseVO toResponse(UserPO user);

    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    UserRoleResponseVO toResponse(UserRolePO userRole);
}
