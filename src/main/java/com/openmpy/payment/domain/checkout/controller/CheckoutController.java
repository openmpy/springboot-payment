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
public class CheckoutController {

    private final OrderRepository orderRepository;
    private final PaymentProcessingService paymentProcessingService;

    @GetMapping("/order")
    public String order(
            @RequestParam("userId") Long userId,
            @RequestParam("courseId") Long courseId,
            @RequestParam("courseName") String courseName,
            @RequestParam("amount") String amount,
            Model model
    ) {
        Order order = Order.builder()
                .amount(BigDecimal.TEN)
                .courseId(courseId)
                .courseName(courseName)
                .userId(userId)
                .requestId(UUID.randomUUID().toString())
                .status(Status.WAIT)
                .build();

        orderRepository.save(order);

        model.addAttribute("courseName", courseName);
        model.addAttribute("requestId", order.getRequestId());
        model.addAttribute("amount", amount);
        model.addAttribute("customerKey", "customerKey-" + userId);
        return "/order.html";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "/checkout.html";
    }

    @GetMapping("/order-requested")
    public String orderRequested() {
        return "/order-requested.html";
    }

    @GetMapping("/fail")
    public String fail() {
        return "/fail.html";
    }

    @PostMapping("/confirm")
    public ResponseEntity<Object> confirmPayment(@RequestBody ConfirmRequest confirmRequest) {
        Order order = orderRepository.findByRequestId(confirmRequest.orderId())
                .orElse(null);

        order.setStatus(Status.REQUESTED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        paymentProcessingService.createPayment(confirmRequest);

        return ResponseEntity.ok(null);
    }
}