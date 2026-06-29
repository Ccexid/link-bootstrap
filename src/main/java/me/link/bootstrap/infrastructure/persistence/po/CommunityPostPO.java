package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

/**
 * 社区帖子持久化对象。
 * <p>
 * 正文和高频计数字段分离保存，后续点赞、评论、收藏只更新计数字段，不依赖每次全表 count。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_post")
public class CommunityPostPO extends TenantBaseDO {

    @TableId
    private Long id;

    @TableField("section_id")
    private Long sectionId;

    @TableField("topic_id")
    private Long topicId;

    @TableField("author_id")
    private Long authorId;

    @TableField("title")
    private String title;

    @TableField("summary")
    private String summary;

    @TableField("content")
    private String content;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("view_count")
    private Long viewCount;

    @TableField("like_count")
    private Long likeCount;

    @TableField("comment_count")
    private Long commentCount;

    @TableField("collect_count")
    private Long collectCount;

    @TableField("pinned")
    private Boolean pinned;

    @TableField("featured")
    private Boolean featured;

    @TableField("`status`")
    private StatusEnum status;
}
