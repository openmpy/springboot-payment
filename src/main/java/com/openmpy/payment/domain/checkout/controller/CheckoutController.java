package com.openmpy.payment.domain.checkout.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import com.openmpy.payment.domain.order.entity.Order;
import com.openmpy.payment.domain.order.entity.constants.Status;
import com.openmpy.payment.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class CheckoutController {

    private final OrderRepository orderRepository;

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

    @GetMapping("/success")
    public String success() {
        return "/success.html";
    }

    @GetMapping("/fail")
    public String fail() {
        return "/fail.html";
    }

    @RequestMapping(value = "/confirm")
    public ResponseEntity<Object> confirmPayment(@RequestBody String jsonBody) throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        ConfirmRequest request = new ConfirmRequest(
                jsonNode.get("paymentKey").asText(),
                jsonNode.get("orderId").asText(),
                jsonNode.get("amount").asText()
        );

        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        RestClient defaultClient = RestClient.create();
        Object object = defaultClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .headers(httpHeaders -> {
                    httpHeaders.set("Authorization", authorizations);
                    httpHeaders.set("Content-Type", "application/json");
                })
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(Object.class);

        return ResponseEntity.ok(object);
    }
}