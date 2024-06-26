package com.openmpy.payment.domain.transaction.dto;

import java.math.BigDecimal;

public record ChargeTransactionRequest(
        Long userId,
        String orderId,
        BigDecimal amount
) {
}
