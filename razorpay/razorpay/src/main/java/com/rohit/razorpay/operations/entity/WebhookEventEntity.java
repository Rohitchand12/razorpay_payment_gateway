package com.rohit.razorpay.operations.entity;

import com.rohit.razorpay.common.entity.BaseEntity;
import com.rohit.razorpay.common.enums.WebhookEventStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@Table(name = "webhook_event")
public class WebhookEventEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false,length = 100)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> payload;

    @Column(nullable = false)
    private String targetUrl;

    @Column(nullable = false)
    private String signature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookEventStatus status;

    @Builder.Default
    private Integer attempts = 0;

    private Integer lastResponseCode;

    @Column(nullable = false,length = 1000)
    private String lastResponseBody;

    private LocalDateTime lastRetryAt;

    private LocalDateTime lastAttemptedAt;

    private LocalDateTime nextRetryAt;

    private LocalDateTime deliveredAt;

}
