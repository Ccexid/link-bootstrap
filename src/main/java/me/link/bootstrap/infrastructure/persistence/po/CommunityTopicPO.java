package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

/**
 * 社区话题持久化对象。
 * <p>
 * 话题归属单个板块，用于帖子发布、内容聚合和运营治理的二级分类。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_topic")
public class CommunityTopicPO extends TenantBaseDO {

    @TableId
    private Long id;

    @TableField("section_id")
    private Long sectionId;

    @TableField("name")
    private String name;

    @TableField("code")
    private String code;

    @TableField("description")
    private String description;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("sort")
    private Integer sort;

    @TableField("`status`")
    private StatusEnum status;
}
