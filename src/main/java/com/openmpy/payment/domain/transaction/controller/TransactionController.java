package com.openmpy.payment.domain.transaction.controller;

import com.openmpy.payment.domain.transaction.dto.ChargeTransactionRequest;
import com.openmpy.payment.domain.transaction.dto.ChargeTransactionResponse;
import com.openmpy.payment.domain.transaction.dto.PaymentTransactionRequest;
import com.openmpy.payment.domain.transaction.dto.PaymentTransactionResponse;
import com.openmpy.payment.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/api/v1/balance/charge")
    public ChargeTransactionResponse charge(
            @RequestBody ChargeTransactionRequest request
    ) {
        return transactionService.charge(request);
    }

    @PostMapping("/api/v1/balance/payment")
    public PaymentTransactionResponse payment(
            @RequestBody PaymentTransactionRequest request
    ) {
        return transactionService.payment(request);
    }
}
