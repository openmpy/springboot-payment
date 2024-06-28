package com.openmpy.payment.domain.checkout.controller;

import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import com.openmpy.payment.domain.checkout.external.PaymentProcessingService;
import com.openmpy.payment.domain.order.entity.Order;
import com.openmpy.payment.domain.order.entity.constants.Status;
import com.openmpy.payment.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class ChargeController {

    private final OrderRepository orderRepository;
    private final PaymentProcessingService paymentProcessingService;

    @GetMapping("/charge-order")
    public String order(
            @RequestParam("userId") Long userId,
            @RequestParam("amount") String amount,
            Model model
    ) {
        Order order = Order.builder()
                .amount(BigDecimal.TEN)
                .userId(userId)
                .requestId(UUID.randomUUID().toString())
                .status(Status.WAIT)
                .build();

        orderRepository.save(order);

        model.addAttribute("requestId", order.getRequestId());
        model.addAttribute("amount", amount);
        model.addAttribute("customerKey", "customerKey-" + userId);
        return "/charge-order.html";
    }

    @GetMapping("/charge-order-requested")
    public String chargeOrderRequested() {
        return "/charge-order-requested.html";
    }

    @GetMapping("/charge-fail")
    public String chargeFail() {
        return "/charge-fail.html";
    }

    @PostMapping("/charge-confirm")
    public ResponseEntity<Object> confirm(@RequestBody ConfirmRequest request) {
        Order order = orderRepository.findByRequestId(request.orderId())
                .orElse(null);

        order.setStatus(Status.REQUESTED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        paymentProcessingService.createCharge(request, false);

        return ResponseEntity.ok(null);
    }
}