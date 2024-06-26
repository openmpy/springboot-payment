package com.openmpy.payment.domain.wallet.controller;

import com.openmpy.payment.domain.wallet.dto.*;
import com.openmpy.payment.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/api/v1/wallets")
    public CreateWalletResponse createWallet(
            @RequestBody CreateWalletRequest request
    ) {
        return walletService.createWallet(request);
    }

    @GetMapping("/api/v1/users/{userId}/wallets")
    public FindWalletResponse findWalletByUserId(
            @PathVariable("userId") Long userId
    ) {
        return walletService.findWalletByUserId(userId);
    }

    @PostMapping("/api/v1/wallets/add-balance")
    public AddBalanceWalletResponse addBalance(
            @RequestBody AddBalanceWalletRequest request
    ) {
        return walletService.addBalance(request);
    }
}
