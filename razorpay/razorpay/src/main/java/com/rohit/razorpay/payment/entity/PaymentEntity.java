package com.rohit.razorpay.payment.entity;

import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.common.enums.PaymentMethod;
import com.rohit.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderRecordEntity order;

    @Column(nullable = false)
    private UUID merchantId;

    // Used to prevent duplicate payment processing on retries
    private String idempotencyKey;

    @Embedded // Embeds Money fields directly into payment table
    @AttributeOverrides({
            @AttributeOverride(name="amountUnits",column=@Column(name = "payment_amount_units")),
            @AttributeOverride(name="currency",column=@Column(name = "payment_amount_currency")),
    })
    private Money amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @JdbcTypeCode(SqlTypes.JSON) // Hibernate should treat this as JSON
    @Column(columnDefinition = "jsonb") // PostgreSQL jsonb column
    private Map<String, Object> methodDetails;

    // Reference returned by bank/payment gateway
    private String bankReference;

    // Gateway/bank error code
    private String errorCode;

    // Human-readable error message
    private String errorDescription;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="amountUnits",column=@Column(name = "refund_amount_units")),
            @AttributeOverride(name="currency",column=@Column(name = "refund_amount_currency")),
    })
    private Money refundAmount;

    // When payment was captured from customer
    private LocalDateTime capturedAt;

    // When funds were settled to merchant
    private LocalDateTime settledAt;

    // When refund was completed
    private LocalDateTime refundedAt;
}