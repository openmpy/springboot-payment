package com.openmpy.payment.domain.transaction.dto;

import java.math.BigDecimal;

public record PaymentTransactionResponse(
        Long walletId,
        BigDecimal balance
) {
}
