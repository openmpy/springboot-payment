package com.openmpy.payment.domain.retry.repository;

import com.openmpy.payment.domain.retry.entity.RetryRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetryRequestRepository extends JpaRepository<RetryRequest, Long> {
}
