package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

/**
 * 社区评论持久化对象。
 * <p>
 * 一级评论和回复复用同一张表，parentId/rootId 描述回复层级，帖子评论数和父评论回复数由服务层维护。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_comment")
public class CommunityCommentPO extends TenantBaseDO {

    @TableId
    private Long id;

    @TableField("post_id")
    private Long postId;

    @TableField("parent_id")
    private Long parentId;

    @TableField("root_id")
    private Long rootId;

    @TableField("author_id")
    private Long authorId;

    @TableField("reply_to_id")
    private Long replyToId;

    @TableField("content")
    private String content;

    @TableField("like_count")
    private Long likeCount;

    @TableField("reply_count")
    private Long replyCount;

    @TableField("`status`")
    private StatusEnum status;
}
