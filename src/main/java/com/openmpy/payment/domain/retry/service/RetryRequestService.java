package com.openmpy.payment.domain.retry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmpy.payment.domain.checkout.dto.ConfirmRequest;
import com.openmpy.payment.domain.checkout.external.PaymentProcessingService;
import com.openmpy.payment.domain.retry.entity.RetryRequest;
import com.openmpy.payment.domain.retry.entity.constants.Status;
import com.openmpy.payment.domain.retry.repository.RetryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class RetryRequestService {

    private final RetryRequestRepository retryRequestRepository;
    private final PaymentProcessingService paymentProcessingService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void retry(Long retryRequestId) {
        RetryRequest request = retryRequestRepository.findById(retryRequestId)
                .orElseThrow();

        ConfirmRequest confirmRequest = objectMapper.readValue(request.getRequestJson(), ConfirmRequest.class);

        try {
            paymentProcessingService.createCharge(confirmRequest, true);
            request.setStatus(Status.SUCCESS);
        } catch (Exception e) {
            request.setRetryCount(request.getRetryCount() + 1);
        } finally {
            request.setUpdatedAt(LocalDateTime.now());
            retryRequestRepository.save(request);
        }
    }
}
