package me.link.bootstrap;

import cn.dev33.satoken.stp.StpUtil;
import me.link.bootstrap.application.service.TestAuditService;
import me.link.bootstrap.domain.log.model.AuditLogDTO;
import me.link.bootstrap.domain.log.spi.AuditLogStorage;
import me.link.bootstrap.infrastructure.context.TenantContextHolder;
import me.link.bootstrap.infrastructure.utils.TraceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuditLogIntegrationTest {

    // 关键点 1: 使用 SpyBean。它会创建一个真实的 Bean 实例，但允许我们对特定方法（如 getById）进行打桩
    // 这样既能触发 @Log 切面，又能控制快照数据
    @MockitoSpyBean
    private TestAuditService testAuditService;

    // 关键点 2: 存储器直接用 MockitoBean，因为我们只需要验证它是否收到了 DTO
    @MockitoBean
    private AuditLogStorage auditLogStorage;

    @BeforeEach
    void setup() {
        TenantContextHolder.setTenantId("888");
        TraceUtils.setupTraceId();

        // 必须 Mock 掉 isEnabled，否则切面会因为 storage 不可用而跳过记录
        when(auditLogStorage.isEnabled()).thenReturn(true);
        // 如果有多个存储器，可能还需要 Mock getName 或 getOrder
        when(auditLogStorage.getName()).thenReturn("MockStorage");
    }

    @Test
    @DisplayName("集成测试：验证修改操作的审计日志链路")
    void testAuditLogFlow() {
        // 1. 准备 Mock 数据
        Long id = 100L;
        TestAuditService.TestUser mockOldUser = new TestAuditService.TestUser();
        mockOldUser.setId(id);
        mockOldUser.setNickname("老旧的数据");
        mockOldUser.setStatus(1);

        // 2. 打桩：给 Spy 对象设置返回值
        // 必须用 doReturn，因为 when 会导致真实的 getById 被调用一次
        doReturn(mockOldUser).when(testAuditService).getById(id);

        // 确保存储器已启用
        when(auditLogStorage.isEnabled()).thenReturn(true);

        // 3. 执行
        TestAuditService.TestUser input = new TestAuditService.TestUser();
        input.setId(id);
        input.setNickname("全新的数据");
        testAuditService.updateTestUser(input);

        // 4. 验证（关键：使用 timeout 应对异步切面）
        ArgumentCaptor<AuditLogDTO> logCaptor = ArgumentCaptor.forClass(AuditLogDTO.class);
        // 这里的 timeout(5000) 会阻塞主线程直到 record 被调用或超时
        verify(auditLogStorage, timeout(5000)).record(logCaptor.capture());

        AuditLogDTO dto = logCaptor.getValue();
        assertThat(dto).isNotNull();
        assertThat(dto.getChanges()).isNotNull();
//        assertThat(dto.getChanges()).isNotEmpty();
    }
}