package com.openmpy.payment.domain.checkout.service;

import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import com.openmpy.payment.domain.checkout.external.PaymentGatewayService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CheckoutServiceIntegrationTest {

    @Autowired
    PaymentGatewayService paymentGatewayService;

    @Test
    void test() {
        paymentGatewayService.confirm(
                new ConfirmRequest(
                        "tgen_20240628163244U5Yg6",
                        "7f0df561-4f99-40b3-9bdb-ea9a33d22005",
                        "2000"
                )
        );
    }
}
