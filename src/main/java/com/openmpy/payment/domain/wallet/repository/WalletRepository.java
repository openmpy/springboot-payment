package com.openmpy.payment.domain.wallet.repository;

import com.openmpy.payment.domain.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    //    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findTopByUserId(Long userId);

    List<Wallet> findAllByUserId(Long userId);
}
