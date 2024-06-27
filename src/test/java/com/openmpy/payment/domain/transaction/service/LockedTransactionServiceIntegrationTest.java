package com.openmpy.payment.domain.transaction.service;

import com.openmpy.payment.domain.transaction.dto.ChargeTransactionRequest;
import com.openmpy.payment.domain.transaction.dto.ChargeTransactionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class LockedTransactionServiceIntegrationTest {

    @Autowired
    LockedTransactionService lockedTransactionService;

    @Test
    void 충전을_동시에_실행한다() throws InterruptedException {
        Long userId = 200L;

        int numOfThread = 20;
        ExecutorService service = Executors.newFixedThreadPool(numOfThread);
        AtomicInteger completedTasks = new AtomicInteger(0);

        for (int i = 0; i < numOfThread; i++) {
            String orderId = UUID.randomUUID().toString();
            ChargeTransactionRequest request = new ChargeTransactionRequest(
                    userId, orderId, BigDecimal.TEN
            );

            service.submit(() -> {
                try {
                    ChargeTransactionResponse response = lockedTransactionService.charge(request);
                    System.out.println(response);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    completedTasks.incrementAndGet();
                }
            });
        }

        service.shutdown();
        boolean finished = service.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println(finished);
    }
}
