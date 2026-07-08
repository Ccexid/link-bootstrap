package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityCommentPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityCommentResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface CommunityCommentService extends IService<CommunityCommentPO> {

    /**
     * 创建社区评论。
     */
    CommunityCommentResponseVO create(CommunityCommentCreateRequest request);
    /**
     * 查询社区评论详情。
     */
    CommunityCommentResponseVO get(Long id);
    /**
     * 分页查询社区评论列表。
     */
    PageResult<CommunityCommentResponseVO> page(CommunityCommentPageRequest request);
    /**
     * 更新社区评论。
     */
    CommunityCommentResponseVO update(Long id, CommunityCommentUpdateRequest request);
    /**
     * 删除社区评论。
     */
    void delete(Long id);
}
