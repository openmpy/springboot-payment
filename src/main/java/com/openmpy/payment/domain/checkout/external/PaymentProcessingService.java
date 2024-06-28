package com.openmpy.payment.domain.checkout.external;

import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import com.openmpy.payment.domain.order.entity.Order;
import com.openmpy.payment.domain.order.entity.constants.Status;
import com.openmpy.payment.domain.order.repository.OrderRepository;
import com.openmpy.payment.domain.transaction.dto.ChargeTransactionRequest;
import com.openmpy.payment.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class PaymentProcessingService {

    private final PaymentGatewayService paymentGatewayService;
    private final TransactionService transactionService;

    private final OrderRepository orderRepository;

    public void createPayment(ConfirmRequest request) {
        paymentGatewayService.confirm(request);
        transactionService.pgPayment();

        approveOrder(request.orderId());
    }

    public void createCharge(ConfirmRequest request) {
        paymentGatewayService.confirm(request);

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

    private void approveOrder(String orderId) {
        Order order = orderRepository.findByRequestId(orderId).orElse(null);
        order.setStatus(Status.APPROVED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}
