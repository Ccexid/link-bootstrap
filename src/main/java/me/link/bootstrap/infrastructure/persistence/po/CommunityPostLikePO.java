package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;

/**
 * 社区帖子点赞关系持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_post_like")
public class CommunityPostLikePO extends TenantBaseDO {

    @TableId
    private Long id;

    @TableField("post_id")
    private Long postId;

    @TableField("user_id")
    private Long userId;
}
