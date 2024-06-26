package com.openmpy.payment.domain.transaction.dto;

import java.math.BigDecimal;

public record PaymentTransactionRequest(
        Long walletId,
        String courseId,
        BigDecimal amount
) {
}
