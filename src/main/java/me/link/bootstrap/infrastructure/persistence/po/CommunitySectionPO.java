package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

/**
 * 社区板块持久化对象。
 * <p>
 * 社区内容按租户隔离，板块作为帖子、评论和推荐策略的基础分类。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_section")
public class CommunitySectionPO extends TenantBaseDO {

    @TableId
    private Long id;

    @TableField("name")
    private String name;

    @TableField("code")
    private String code;

    @TableField("description")
    private String description;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("parent_id")
    private Long parentId;

    @TableField("sort")
    private Integer sort;

    @TableField("`status`")
    private StatusEnum status;
}
