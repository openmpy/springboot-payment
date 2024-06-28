package com.openmpy.payment.domain.retry.entity;

import com.openmpy.payment.domain.retry.entity.constants.Status;
import com.openmpy.payment.domain.retry.entity.constants.Type;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RetryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String requestJson;

    private String requestId;

    @Setter
    private Integer retryCount;

    private String errorResponse;

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;

    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime updatedAt;

    public RetryRequest(String requestJson, String requestId, String errorResponse, Type type) {
        this.requestJson = requestJson;
        this.requestId = requestId;
        this.retryCount = 0;
        this.errorResponse = errorResponse;
        this.status = Status.IN_PROGRESS;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
