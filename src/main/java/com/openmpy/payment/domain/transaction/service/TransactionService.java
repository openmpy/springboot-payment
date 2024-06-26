package com.openmpy.payment.domain.transaction.service;

import com.openmpy.payment.domain.transaction.dto.ChargeTransactionRequest;
import com.openmpy.payment.domain.transaction.dto.ChargeTransactionResponse;
import com.openmpy.payment.domain.transaction.dto.PaymentTransactionRequest;
import com.openmpy.payment.domain.transaction.dto.PaymentTransactionResponse;
import com.openmpy.payment.domain.transaction.entity.Transaction;
import com.openmpy.payment.domain.transaction.repository.TransactionRepository;
import com.openmpy.payment.domain.wallet.dto.AddBalanceWalletRequest;
import com.openmpy.payment.domain.wallet.dto.AddBalanceWalletResponse;
import com.openmpy.payment.domain.wallet.dto.FindWalletResponse;
import com.openmpy.payment.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TransactionService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    public ChargeTransactionResponse charge(ChargeTransactionRequest request) {
        if (transactionRepository.findTransactionByOrderId(request.orderId()).isPresent()) {
            throw new RuntimeException("이미 충전된 거래입니다.");
        }

        FindWalletResponse findWalletResponse = walletService.findWalletByUserId(request.userId());

        if (findWalletResponse == null) {
            throw new RuntimeException("사용자 지갑이 존재하지 않습니다.");
        }

        AddBalanceWalletResponse wallet = walletService.addBalance(new AddBalanceWalletRequest(
                findWalletResponse.id(),
                request.amount()
        ));

        Transaction transaction = Transaction.createChargeTransaction(
                request.userId(),
                wallet.id(),
                request.orderId(),
                request.amount()
        );

        transactionRepository.save(transaction);
        return new ChargeTransactionResponse(wallet.id(), wallet.balance());
    }

    public PaymentTransactionResponse payment(PaymentTransactionRequest request) {
        if (transactionRepository.findTransactionByOrderId(request.courseId()).isPresent()) {
            throw new RuntimeException("이미 결제된 강좌입니다.");
        }

        FindWalletResponse findWalletResponse = walletService.findWalletByWalletId(request.walletId());

        AddBalanceWalletResponse wallet = walletService.addBalance(new AddBalanceWalletRequest(
                findWalletResponse.id(),
                request.amount().negate()
        ));

        Transaction transaction = Transaction.createPaymentTransaction(
                wallet.userId(),
                wallet.id(),
                request.courseId(),
                request.amount()
        );

        transactionRepository.save(transaction);
        return new PaymentTransactionResponse(wallet.id(), wallet.balance());
    }
}
