package com.openmpy.payment.domain.transaction.dto;

import java.math.BigDecimal;

public record ChargeTransactionResponse(
        Long walletId,
        BigDecimal balance
) {
}
