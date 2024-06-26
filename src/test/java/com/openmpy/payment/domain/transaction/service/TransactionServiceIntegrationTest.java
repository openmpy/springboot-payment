package com.openmpy.payment.domain.transaction.service;

import com.openmpy.payment.domain.transaction.dto.ChargeTransactionRequest;
import com.openmpy.payment.domain.transaction.dto.ChargeTransactionResponse;
import com.openmpy.payment.domain.transaction.dto.PaymentTransactionRequest;
import com.openmpy.payment.domain.transaction.dto.PaymentTransactionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TransactionServiceIntegrationTest {

    @Autowired
    TransactionService transactionService;

    @Test
    @Transactional
    void 결제를_생성한다() {
        // given
        PaymentTransactionRequest request = new PaymentTransactionRequest(
                2L, "course-1", new BigDecimal(10)
        );

        // when
        PaymentTransactionResponse response = transactionService.payment(request);

        // then
        Assertions.assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void 충전_진행() {
        Long userId = 200L;
        String orderId = "orderId-1";

        // given
        ChargeTransactionRequest request = new ChargeTransactionRequest(
                userId, orderId, BigDecimal.TEN
        );

        // when
        ChargeTransactionResponse response = transactionService.charge(request);

        // then
        System.out.println(response);
    }

    @Test
    void 충전을_동시에_실행한다() throws InterruptedException {
        Long userId = 200L;
        String orderId = "orderId-2";

        ChargeTransactionRequest request = new ChargeTransactionRequest(
                userId, orderId, BigDecimal.TEN
        );

        int numOfThread = 20;
        ExecutorService service = Executors.newFixedThreadPool(numOfThread);
        AtomicInteger completedTasks = new AtomicInteger(0);

        for (int i = 0; i < numOfThread; i++) {
            service.submit(() -> {
                try {
                    ChargeTransactionResponse response = transactionService.charge(request);
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
