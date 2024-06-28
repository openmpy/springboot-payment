package com.openmpy.payment.domain.checkout.external;

import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@RequiredArgsConstructor
@Service
public class PaymentGatewayService {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    @Value("${pg.url}")
    public String URL;

    public void confirm(ConfirmRequest request) {
        String widgetSecretKey = SECRET_KEY;
        byte[] encodedBytes = ENCODER.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(3));
        ClientHttpRequestFactory factory = ClientHttpRequestFactories.get(settings);
        RestClient defaultClient = RestClient.builder()
                .requestFactory(factory)
                .build();

        ResponseEntity<Object> object = defaultClient.post()
                .uri(URL)
                .headers(httpHeaders -> {
                    httpHeaders.set("Authorization", authorizations);
                    httpHeaders.set("Content-Type", "application/json");
                })
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(Object.class);

        if (object.getStatusCode().isError()) {
            throw new IllegalStateException("결제 요청에 실패하셨습니다.");
        }
    }
}
