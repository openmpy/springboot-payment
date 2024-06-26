package com.openmpy.payment.domain.wallet.service;

import com.openmpy.payment.domain.wallet.dto.CreateWalletRequest;
import com.openmpy.payment.domain.wallet.dto.CreateWalletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WalletServiceIntegrationTest {

    @Autowired
    WalletService walletService;

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
}
