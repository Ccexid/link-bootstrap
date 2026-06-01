package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.TenantEntity;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 租户对象转换器（Entity <-> PO）
 * <p>
 * 绑定全局统一的 BaseConverter 配置。
 * S (Source) = LinkEntity (领域实体)
 * T (Target) = LinkPO (数据库持久化对象)
 * </p>
 *
 * @author ccexid
 */
@Mapper(config = BaseConverter.class)
public interface TenantConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    TenantPO convert(TenantEntity tenantEntity);

    default TenantEntity reverseConvert(TenantPO tenantPO) {
        if (tenantPO == null) {
            return null;
        }
        return TenantEntity.restore(
                tenantPO.getId(),
                tenantPO.getName(),
                tenantPO.getContactUserId(),
                tenantPO.getContactName(),
                tenantPO.getContactMobile(),
                tenantPO.getStatus(),
                tenantPO.getWebsites(),
                tenantPO.getPackageId(),
                tenantPO.getExpireTime(),
                tenantPO.getAccountCount()
        );
    }


    /**
     * 列表映射：批量从源对象列表转换为目标对象列表
     * <p>
     * 如果源列表为 null，则返回空列表（Collections.emptyList()）。
     * 列表中的 null 元素会被跳过，不会出现在结果中。
     * </p>
     *
     * @param sourceList 源对象列表
     * @return 目标对象列表，源列表为 null 时返回空列表
     */
    List<TenantPO> convertList(List<TenantEntity> sourceList);

    /**
     * 逆向列表映射：批量从目标对象列表转换回源对象列表
     * <p>
     * 自动继承 {@link #convertList(List)} 的反向映射配置。
     * 如果目标列表为 null，则返回空列表（Collections.emptyList()）。
     * 列表中的 null 元素会被跳过，不会出现在结果中。
     * </p>
     *
     * @param targetList 目标对象列表
     * @return 源对象列表，目标列表为 null 时返回空列表
     */
    List<TenantEntity> reverseConvertList(List<TenantPO> targetList);
}
