package com.openmpy.payment.domain.wallet.dto;

import java.math.BigDecimal;

public record CreateWalletResponse(
        Long id,
        Long userId,
        BigDecimal balance
) {
}
