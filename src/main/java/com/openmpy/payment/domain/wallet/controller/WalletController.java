package com.openmpy.payment.domain.wallet.controller;

import com.openmpy.payment.domain.wallet.dto.CreateWalletRequest;
import com.openmpy.payment.domain.wallet.dto.CreateWalletResponse;
import com.openmpy.payment.domain.wallet.dto.FindWalletResponse;
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
}
