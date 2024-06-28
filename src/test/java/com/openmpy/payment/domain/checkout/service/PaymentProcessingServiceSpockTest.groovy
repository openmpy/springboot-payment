package com.openmpy.payment.domain.checkout.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.openmpy.payment.domain.checkout.dto.ConfirmRequest
import com.openmpy.payment.domain.checkout.external.PaymentGatewayService
import com.openmpy.payment.domain.checkout.external.PaymentProcessingService
import com.openmpy.payment.domain.order.entity.Order
import com.openmpy.payment.domain.order.repository.OrderRepository
import com.openmpy.payment.domain.retry.repository.RetryRequestRepository
import com.openmpy.payment.domain.transaction.service.TransactionService
import org.springframework.web.client.RestClientException
import spock.lang.Specification

class PaymentProcessingServiceSpockTest extends Specification {

    PaymentProcessingService paymentProcessingService
    PaymentGatewayService paymentGatewayService = Mock()
    TransactionService transactionService = Mock()
    OrderRepository orderRepository = Mock()
    RetryRequestRepository retryRequestRepository = Mock()
    ObjectMapper objectMapper = new ObjectMapper()

    def setup() {
        paymentProcessingService = new PaymentProcessingService(
                paymentGatewayService, transactionService, orderRepository, retryRequestRepository, objectMapper
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

    def "Retry & 걀제 성공시 충전된다."() {
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
        paymentProcessingService.createCharge(confirmRequest, false)

        then:
        1 * paymentGatewayService.confirm(confirmRequest)
        1 * transactionService.charge(_)
        1 * orderRepository.save(order)
    }

    def "Timeout이 발생했을 때 RetryRequest를 저장하고 오류를 발생시킨다."() {
        given:
        ConfirmRequest confirmRequest = new ConfirmRequest(
                "paymentKey",
                "orderId",
                "1000"
        )

        // mock
        Order order = new Order()
        orderRepository.findByRequestId(confirmRequest.orderId()) >> Optional.of(order)
        paymentGatewayService.confirm(_) >> {
            RestClientException ex = new RestClientException(
                    "Error while extracting response for type [java.lang.Object] and content type [application/octet-stream]",
                    new SocketTimeoutException("Read timed out")
            )
            throw ex
        }

        when:
        paymentProcessingService.createCharge(confirmRequest, false)

        then:
        def ex = thrown(RestClientException)
        ex.printStackTrace()
        0 * transactionService.charge(_)
        0 * orderRepository.save(order)
        1 * retryRequestRepository.save(_)
    }
}
