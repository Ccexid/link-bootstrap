package me.link.bootstrap.application.service;

import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.annotation.Log;
import me.link.bootstrap.infrastructure.annotation.LogField;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class TestAuditService {

    @Data
    public static class TestUser {
        private Long id;

        @LogField("用户昵称") // 明确审计日志中的显示名称
        private String nickname;

        @LogField("账号状态")
        private Integer status;

        // 建议加上不记录日志的敏感字段示例
        private String password;
    }

    /**
     * 获取原始快照 (切面会反射调用此方法)
     * 在真实场景中，这里应该查数据库：return userMapper.selectById(id);
     */
    public TestUser getById(Long id) {
        log.info("[Mock] getById 被调用，ID: {}", id);
        return null;
    }

    /**
     * 更新操作
     * 优化点：
     * 1. operation 修正为模板模式，增强可读性
     * 2. businessId 确保指向入参或返回值的 ID
     * 3. 直接返回对象，简化切面的 #result 逻辑
     */
    @Log(
            module = "用户管理",
            operation = "修改用户：从 [#{#user.nickname}] 变更为 [#{#result.nickname}]",
            businessId = "#user.id",
            serviceName = "testAuditService",
            isDiff = true
    )
    public TestUser updateTestUser(TestUser user) {
        // 1. 业务逻辑处理
        log.info("[Business] 开始执行更新逻辑, ID: {}", user.getId());

        // 2. 模拟更新成功后的返回对象
        // 注意：在真实代码中，你可能已经修改了数据库，这里返回的是最新的实体
        TestUser updatedUser = new TestUser();
        updatedUser.setId(user.getId());
        updatedUser.setNickname(user.getNickname()); // 假设入参带了新名字
        updatedUser.setStatus(0); // 假设逻辑里修改了状态

        return updatedUser;
    }
}