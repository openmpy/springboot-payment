package com.openmpy.payment.domain.checkout.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class CheckoutController {

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