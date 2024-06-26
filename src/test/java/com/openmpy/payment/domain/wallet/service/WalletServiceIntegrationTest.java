package com.openmpy.payment.domain.wallet.service;

import com.openmpy.payment.domain.wallet.dto.CreateWalletRequest;
import com.openmpy.payment.domain.wallet.dto.CreateWalletResponse;
import com.openmpy.payment.domain.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WalletServiceIntegrationTest {

    @Autowired
    WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    @Transactional
    void 지갑을_생성한다() {
        // given
        CreateWalletRequest request = new CreateWalletRequest(200L);

        // when
        CreateWalletResponse response = walletService.createWallet(request);

        // then
        Assertions.assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void 동시에_여러건의_계좌가_생성된다면_잘_될까() throws InterruptedException {
        Long userId = 10L;
        CreateWalletRequest request = new CreateWalletRequest(userId);

        int numOfThread = 20;
        ExecutorService service = Executors.newFixedThreadPool(numOfThread);
        AtomicInteger completedTasks = new AtomicInteger(0);

        for (int i = 0; i < numOfThread; i++) {
            service.submit(() -> {
                try {
                    walletService.createWallet(request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    completedTasks.incrementAndGet();
                }
            });
        }

        service.shutdown();
        boolean finished = service.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println(finished);
        System.out.println(walletRepository.findAllByUserId(userId));
    }
}
