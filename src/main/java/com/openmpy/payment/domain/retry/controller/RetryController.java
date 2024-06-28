package com.openmpy.payment.domain.retry.controller;

import com.openmpy.payment.domain.retry.service.RetryRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RetryController {

    private final RetryRequestService retryRequestService;

    @PostMapping("/api/v1/retry-request/{retryId}")
    public void retry(@PathVariable("retryId") Long retryId) {
        retryRequestService.retry(retryId);
    }
}
