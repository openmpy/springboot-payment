package com.openmpy.payment.domain.transaction.service;

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
}
