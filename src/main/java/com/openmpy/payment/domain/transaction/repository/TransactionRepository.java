package com.openmpy.payment.domain.transaction.repository;

import com.openmpy.payment.domain.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTransactionByOrderId(String orderId);
}
