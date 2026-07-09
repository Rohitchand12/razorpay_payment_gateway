package com.rohit.razorpay.operations.entity;

import com.rohit.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "settlement_payment")
public class SettlementPaymentEntity extends BaseEntity {
    @EmbeddedId
    private SettlementPaymentId id; // to create a composite key.

//    @MapsId()
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "settlement_id")
//    private SettlementEntity settlement;

}
