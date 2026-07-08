package me.link.bootstrap.domain;

import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainReliabilityTest {

    /**
     * 验证 pageResultShouldNormalizeNullValues 场景。
     */
    @Test
    void pageResultShouldNormalizeNullValues() {
        PageResult<String> pageResult = new PageResult<>(null, null);

        assertThat(pageResult.records()).isEmpty();
        assertThat(pageResult.total()).isZero();
    }

    /**
     * 验证 pageResultShouldProtectRecordsFromExternalMutation 场景。
     */
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

}
