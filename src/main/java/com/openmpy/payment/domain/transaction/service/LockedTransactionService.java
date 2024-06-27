package com.openmpy.payment.domain.transaction.service;

import com.openmpy.payment.domain.transaction.dto.ChargeTransactionRequest;
import com.openmpy.payment.domain.transaction.dto.ChargeTransactionResponse;
import com.openmpy.payment.domain.wallet.dto.FindWalletResponse;
import com.openmpy.payment.domain.wallet.service.WalletLockerService;
import com.openmpy.payment.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LockedTransactionService {

    private final WalletService walletService;
    private final WalletLockerService walletLockerService;
    private final TransactionService transactionService;

    public ChargeTransactionResponse charge(ChargeTransactionRequest request) {
        FindWalletResponse findWalletResponse = walletService.findWalletByUserId(request.userId());

        WalletLockerService.Lock lock = walletLockerService.acquireLock(findWalletResponse.id());

        if (lock == null) {
            log.info("Lock 취득 실패");
            throw new IllegalStateException("cannot get lock");
        }

        try {
            log.info("Lock 취득 성공");
            return transactionService.charge(request);
        } finally {
            walletLockerService.releaseLock(lock);
        }
    }
}
