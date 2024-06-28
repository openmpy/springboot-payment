package com.openmpy.payment.domain.checkout.dto;

public record ConfirmRequest(
        String paymentKey,
        String orderId,
        String amount
) {

}
