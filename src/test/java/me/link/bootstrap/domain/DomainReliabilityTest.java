package me.link.bootstrap.domain;

import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.domain.factory.RoleMenuFactory;
import me.link.bootstrap.domain.factory.UserRoleFactory;
import me.link.bootstrap.domain.valueobject.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainReliabilityTest {

    @Test
    void pageResultShouldNormalizeNullValues() {
        PageResult<String> pageResult = new PageResult<>(null, null);

        assertThat(pageResult.records()).isEmpty();
        assertThat(pageResult.total()).isZero();
    }

    @Test
    void pageResultShouldProtectRecordsFromExternalMutation() {
        List<String> records = new java.util.ArrayList<>();
        records.add("alice");

        PageResult<String> pageResult = new PageResult<>(records, 1L);
        records.add("bob");

        assertThat(pageResult.records()).containsExactly("alice");
        assertThatThrownBy(() -> pageResult.records().add("charlie"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void userRoleShouldRequireTenantId() {
        assertThatThrownBy(() -> UserRoleFactory.create(1L, 2L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户角色关联tenantId必须大于0");

        UserRoleEntity userRole = UserRoleFactory.create(1L, 2L, 3L);

        assertThat(userRole.getTenantId()).isEqualTo(3L);
    }

    @Test
    void roleMenuShouldRequireTenantId() {
        assertThatThrownBy(() -> RoleMenuFactory.create(1L, 2L, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("角色菜单关联tenantId必须大于0");

        RoleMenuEntity roleMenu = RoleMenuFactory.create(1L, 2L, 3L);

        assertThat(roleMenu.getTenantId()).isEqualTo(3L);
    }

}
