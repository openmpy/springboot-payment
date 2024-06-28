package com.openmpy.payment.domain.checkout.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import com.openmpy.payment.domain.order.entity.Order;
import com.openmpy.payment.domain.order.entity.constants.Status;
import com.openmpy.payment.domain.order.repository.OrderRepository;
import com.openmpy.payment.domain.retry.entity.RetryRequest;
import com.openmpy.payment.domain.retry.entity.constants.Type;
import com.openmpy.payment.domain.retry.repository.RetryRequestRepository;
import com.openmpy.payment.domain.transaction.dto.ChargeTransactionRequest;
import com.openmpy.payment.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentProcessingService {

    private final PaymentGatewayService paymentGatewayService;
    private final TransactionService transactionService;

    private final OrderRepository orderRepository;
    private final RetryRequestRepository retryRequestRepository;

    private final ObjectMapper objectMapper;

    public void createPayment(ConfirmRequest request) {
        paymentGatewayService.confirm(request);
        transactionService.pgPayment();

        approveOrder(request.orderId());
    }

    public void createCharge(ConfirmRequest request, boolean isRetry) {
        try {
            paymentGatewayService.confirm(request);
        } catch (Exception e) {
            log.error("caught exception on createCharge", e);

            if (!isRetry && e instanceof RestClientException && e.getCause() instanceof SocketTimeoutException) {
                createRetryRequest(request, e);
            }
            throw e;
        }

        Order order = orderRepository.findByRequestId(request.orderId()).orElse(null);
        transactionService.charge(
                new ChargeTransactionRequest(
                        order.getUserId(),
                        request.orderId(),
                        new BigDecimal(request.amount())
                )
        );

        approveOrder(request.orderId());
    }

    @SneakyThrows
    private void createRetryRequest(ConfirmRequest request, Exception e) {
        RetryRequest retryRequest = new RetryRequest(
                objectMapper.writeValueAsString(request),
                request.orderId(),
                e.getMessage(),
                Type.CONFIRM
        );

        retryRequestRepository.save(retryRequest);
    }

    private void approveOrder(String orderId) {
        Order order = orderRepository.findByRequestId(orderId).orElse(null);
        order.setStatus(Status.APPROVED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}
