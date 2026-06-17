package com.rohit.razorpay.payment.entity;

import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.common.enums.RefundStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "refund")
public class RefundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @Column(nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @JdbcTypeCode(SqlTypes.JSON) // Hibernate should treat this as JSON
    @Column(columnDefinition = "jsonb") // PostgreSQL jsonb column
    private Map<String, Object> notes;

    private String bankReference;

    private String errorCode;

    private String errorDescription;

    private LocalDateTime processedAt;

    private LocalDateTime createdAt;
}