package com.openmpy.payment.domain.order.repository;

import com.openmpy.payment.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserId(Long userId);

    Optional<Order> findByRequestId(String requestId);
}
