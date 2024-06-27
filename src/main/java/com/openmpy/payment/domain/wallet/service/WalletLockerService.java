package com.openmpy.payment.domain.wallet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletLockerService {

    private static final Long LOCK_TTL = 30_000L;
    private static final String LOCK_PREFIX = "wallet-lock:";

    private final RedisTemplate<String, Lock> redisTemplate;

    public Lock acquireLock(Long walletId) {
        Lock lock = new Lock(generateKey(walletId), "contents");

        Boolean result = redisTemplate.opsForValue().setIfAbsent(
                lock.key(),
                lock,
                Duration.ofMillis(LOCK_TTL)
        );

        if (result == Boolean.TRUE) {
            return lock;
        }

        return null;
    }

    public void releaseLock(Lock lock) {
        if (lock == null) {
            return;
        }

        redisTemplate.delete(lock.key());
    }

    private String generateKey(Long key) {
        return LOCK_PREFIX + key;
    }

    public record Lock(
            String key,
            String contents
    ) {
    }
}
