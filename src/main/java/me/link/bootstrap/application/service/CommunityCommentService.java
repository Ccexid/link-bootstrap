package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.CommunityCommentPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityCommentPO;
import me.link.bootstrap.infrastructure.persistence.po.CommunityPostPO;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentPageRequest;
import me.link.bootstrap.interfaces.dto.request.community.comment.CommunityCommentUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.CommunityCommentResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface CommunityCommentService extends IService<CommunityCommentPO> {

    CommunityCommentResponseVO create(CommunityCommentCreateRequest request);
    CommunityCommentResponseVO get(Long id);
    PageResult<CommunityCommentResponseVO> page(CommunityCommentPageRequest request);
    CommunityCommentResponseVO update(Long id, CommunityCommentUpdateRequest request);
    void delete(Long id);
}
