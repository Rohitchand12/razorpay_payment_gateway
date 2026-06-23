package com.rohit.razorpay.payment.entity;
import com.rohit.razorpay.common.entity.Money;
import com.rohit.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name="order_record")
@Getter
@Builder
public class OrderRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="merchant_id", nullable = false)
    private UUID merchantId; // no relationship here because Payment is completely different service

    @Column(length = 100)
    private String receipt;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> notes;

    @Column(nullable = false)
    private LocalDateTime expiresAt ;
}
