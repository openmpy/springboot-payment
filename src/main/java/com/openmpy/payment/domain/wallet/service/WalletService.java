package com.openmpy.payment.domain.wallet.service;

import com.openmpy.payment.domain.wallet.dto.*;
import com.openmpy.payment.domain.wallet.entity.Wallet;
import com.openmpy.payment.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class WalletService {

    private static final BigDecimal BALANCE_LIMIT = new BigDecimal(100_000);

    private final WalletRepository walletRepository;

    public CreateWalletResponse createWallet(CreateWalletRequest request) {
        boolean isPresent = walletRepository.findByUserId(request.userId()).isPresent();
        if (isPresent) {
            throw new RuntimeException("지갑을 이미 가지고 있습니다.");
        }

        Wallet wallet = walletRepository.save(new Wallet(request.userId()));

        return new CreateWalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }

    @Transactional(readOnly = true)
    public FindWalletResponse findWalletByUserId(Long userId) {

        return walletRepository.findByUserId(userId)
                .map(wallet -> new FindWalletResponse(
                        wallet.getId(),
                        wallet.getUserId(),
                        wallet.getBalance(),
                        wallet.getCreatedAt(),
                        wallet.getUpdatedAt()
                ))
                .orElse(null);
    }

    public AddBalanceWalletResponse addBalance(AddBalanceWalletRequest request) {
        Wallet wallet = walletRepository.findById(request.walletId()).orElseThrow(
                () -> new RuntimeException("지갑이 없습니다.")
        );

        BigDecimal balance = wallet.getBalance();
        balance = balance.add(request.amount());
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("잔액이 충분하지 않습니다.");
        }
        if (BALANCE_LIMIT.compareTo(balance) < 0) {
            throw new RuntimeException("한도를 초과했습니다.");
        }

        wallet.setBalance(balance);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        return new AddBalanceWalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}
