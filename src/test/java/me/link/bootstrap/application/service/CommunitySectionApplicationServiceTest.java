package me.link.bootstrap.application.service;

import me.link.bootstrap.infrastructure.persistence.internal.CommunitySectionInternalService;
import me.link.bootstrap.infrastructure.persistence.po.CommunitySectionPO;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionCreateRequest;
import me.link.bootstrap.interfaces.dto.request.community.section.CommunitySectionUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommunitySectionApplicationServiceTest {

    private final CommunitySectionInternalService communitySectionInternalService = mock(CommunitySectionInternalService.class);
    private final CommunitySectionApplicationService communitySectionApplicationService = new CommunitySectionApplicationService(
            communitySectionInternalService
    );

    @Test
    void shouldRejectMissingParentWhenCreatingSection() {
        when(communitySectionInternalService.getById(2L)).thenReturn(null);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> communitySectionApplicationService.create(createRequest(2L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        }

        verify(communitySectionInternalService, never()).save(any(CommunitySectionPO.class));
    }

    @Test
    void shouldRejectParentOutsideCurrentTenantWhenCreatingSection() {
        CommunitySectionPO parent = new CommunitySectionPO();
        parent.setId(2L);
        parent.setTenantId(20L);
        when(communitySectionInternalService.getById(2L)).thenReturn(parent);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> communitySectionApplicationService.create(createRequest(2L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.COMMUNITY_SECTION_NOT_FOUND);
        }

        verify(communitySectionInternalService, never()).save(any(CommunitySectionPO.class));
    }

    @Test
    void shouldRejectSelfParentWhenUpdatingSection() {
        CommunitySectionPO section = new CommunitySectionPO();
        section.setId(1L);
        section.setTenantId(10L);
        section.setName("旧板块");
        section.setCode("old");
        when(communitySectionInternalService.getById(1L)).thenReturn(section);

        try (MockedStatic<SecurityHelper> securityHelper = mockStatic(SecurityHelper.class)) {
            securityHelper.when(SecurityHelper::getRequiredTenantId).thenReturn(10L);

            assertThatThrownBy(() -> communitySectionApplicationService.update(1L, updateRequest(1L)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("社区板块不能设置自身为父板块");
        }

        verify(communitySectionInternalService, never()).updateById(any(CommunitySectionPO.class));
    }

    private CommunitySectionCreateRequest createRequest(Long parentId) {
        CommunitySectionCreateRequest request = new CommunitySectionCreateRequest();
        request.setName("推荐");
        request.setCode("recommend");
        request.setParentId(parentId);
        return request;
    }

    private CommunitySectionUpdateRequest updateRequest(Long parentId) {
        CommunitySectionUpdateRequest request = new CommunitySectionUpdateRequest();
        request.setName("推荐");
        request.setCode("recommend");
        request.setParentId(parentId);
        return request;
    }
}
