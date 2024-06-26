package com.openmpy.payment.domain.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AddBalanceWalletResponse(
        Long id,
        Long userId,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
