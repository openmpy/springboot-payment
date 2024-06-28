package com.openmpy.payment.domain.order.entity;

import com.openmpy.payment.domain.order.entity.constants.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(
        name = "temp_order",
        indexes = {
                @Index(name = "index_user_id", columnList = "userId")
        }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private BigDecimal amount;

    @Column(unique = true)
    private String requestId;

    private Long courseId;

    private String courseName;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Order(Long userId, BigDecimal amount, String requestId, Long courseId, String courseName, Status status) {
        this.userId = userId;
        this.amount = amount;
        this.requestId = requestId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
