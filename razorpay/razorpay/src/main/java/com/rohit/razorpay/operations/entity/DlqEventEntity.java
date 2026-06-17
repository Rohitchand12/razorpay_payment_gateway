package com.rohit.razorpay.operations.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "dlq_event")
public class DlqEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    private WebhookEventEntity webhookEvent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> payload;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false,length = 1000)
    private String finalError;

    private LocalDateTime movedAt;

    private LocalDateTime replayedAt;
}
