package com.openmpy.payment.domain.checkout.external;

import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import com.openmpy.payment.domain.order.entity.Order;
import com.openmpy.payment.domain.order.entity.constants.Status;
import com.openmpy.payment.domain.order.repository.OrderRepository;
import com.openmpy.payment.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PaymentProcessingService {

    private final PaymentGatewayService paymentGatewayService;
    private final TransactionService transactionService;

    private final OrderRepository orderRepository;

    public void createPayment(ConfirmRequest confirmRequest) {
        paymentGatewayService.confirm(confirmRequest);
        transactionService.pgPayment();

        Order order = orderRepository.findByRequestId(confirmRequest.orderId()).orElse(null);
        order.setStatus(Status.APPROVED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}
