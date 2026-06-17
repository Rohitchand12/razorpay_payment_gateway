package com.rohit.razorpay.operations.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "settlement_payment")
public class SettlementPaymentEntity {
    @EmbeddedId
    private SettlementPaymentId id; // to create a composite key.

    @MapsId()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private SettlementEntity settlement;

}
