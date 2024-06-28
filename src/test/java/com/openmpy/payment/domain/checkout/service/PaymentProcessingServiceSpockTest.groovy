package com.openmpy.payment.domain.checkout.service

import com.openmpy.payment.domain.checkout.dto.ConfirmRequest
import com.openmpy.payment.domain.checkout.external.PaymentGatewayService
import com.openmpy.payment.domain.checkout.external.PaymentProcessingService
import com.openmpy.payment.domain.order.entity.Order
import com.openmpy.payment.domain.order.repository.OrderRepository
import com.openmpy.payment.domain.transaction.service.TransactionService
import spock.lang.Specification

class PaymentProcessingServiceSpockTest extends Specification {

    PaymentProcessingService paymentProcessingService
    PaymentGatewayService paymentGatewayService = Mock()
    TransactionService transactionService = Mock()
    OrderRepository orderRepository = Mock()

    def setup() {
        paymentProcessingService = new PaymentProcessingService(
                paymentGatewayService, transactionService, orderRepository
        )
    }

    def "PG 결제 성공시 결제 기록이 생성된다."() {
        given:
        ConfirmRequest confirmRequest = new ConfirmRequest(
                "paymentKey",
                "orderId",
                "1000"
        )

        // mock
        Order order = new Order()
        orderRepository.findByRequestId(confirmRequest.orderId()) >> Optional.of(order)

        when:
        paymentProcessingService.createPayment(confirmRequest)

        then:
        1 * paymentGatewayService.confirm(confirmRequest)
        1 * transactionService.pgPayment()
        1 * orderRepository.save(order)
    }
}
