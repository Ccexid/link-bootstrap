package me.link.bootstrap.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@DisplayName("业务 ID 生成器测试")
@Slf4j
class IdUtilsTest {

    @Autowired
    private IdUtils idUtils;

    @Test
    @DisplayName("测试合同号生成（按天重置模式）")
    void testNextDailyId() {
        // 生成第一个 ID
        String id1 = idUtils.nextId("HT", 4, true);
        // 生成第二个 ID
        String id2 = idUtils.nextId("HT", 4, true);

        log.info("生成的合同1: {}", id1);
        log.info("生成的合同2: {}", id2);

        Assertions.assertTrue(id1.startsWith("HT"));
        Assertions.assertEquals(id1.length(), id2.length());
        // 验证自增性：后一个 ID 的数字部分应该比前一个大
        long num1 = Long.parseLong(id1.substring(10));
        long num2 = Long.parseLong(id2.substring(10));
        Assertions.assertEquals(num1 + 1, num2);
    }

    @Test
    @DisplayName("测试房源号生成（全局递增模式）")
    void testNextGlobalId() {
        String id = idUtils.nextId("FD", 15, false);
        log.info("生成的房源ID: {}", id);

        Assertions.assertTrue(id.startsWith("FD"));
        // 全局模式 ID 长度 = 前缀(2) + 数字位(15) = 17
        Assertions.assertEquals(17, id.length());
    }

    @Test
    @DisplayName("高并发下的唯一性测试")
    void testConcurrency() throws InterruptedException {
        int threadCount = 50;
        int iterations = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 使用线程安全的 Set 记录生成的 ID
        Set<String> idSet = Collections.synchronizedSet(new HashSet<>());

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        String id =  idUtils.nextId("CONCUR", 6, true);
                        log.info("生成的合同ID: {}", id);
                        idSet.add(id);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 验证生成的 ID 总数是否等于 线程数 * 循环次数 (如果不唯一，Set 的 size 会变小)
        Assertions.assertEquals(threadCount * iterations, idSet.size(), "并发下生成了重复的 ID！");
    }
}