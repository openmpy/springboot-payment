package com.openmpy.payment.domain.wallet.dto;

import java.math.BigDecimal;

public record AddBalanceWalletRequest(
        Long walletId,
        BigDecimal amount
) {
}
