package com.rohit.razorpay.payment.entity;

import com.rohit.razorpay.common.enums.PaymentActor;
import com.rohit.razorpay.common.enums.PaymentEvent;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transition_log")
public class PaymentTransitionLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    private String fromStatus;

    private String toStatus;

    @Enumerated(EnumType.STRING)
    private PaymentEvent eventType;

    @Enumerated(EnumType.STRING)
    private PaymentActor actor;

    private String reason;

    private LocalDateTime occurredAt;
}