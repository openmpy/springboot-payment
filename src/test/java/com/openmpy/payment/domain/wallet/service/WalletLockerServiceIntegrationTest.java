package com.openmpy.payment.domain.wallet.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WalletLockerServiceIntegrationTest {

    @Autowired
    WalletLockerService walletLockerService;

    @Test
    void test_acquire_lock() {
        WalletLockerService.Lock lock = walletLockerService.acquireLock(1L);
        System.out.println(lock);
        System.out.println(walletLockerService.acquireLock(1L));

        walletLockerService.releaseLock(lock);
        System.out.println(walletLockerService.acquireLock(1L));
    }
}
