package com.rohit.razorpay.merchant.entity;

import com.rohit.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "customer", indexes = {
        @Index(name = "idx_customer_merchant_id",columnList = "merchant_id"),
        @Index(name = "idx_customer_email",columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private MerchantEntity merchant;

    @Column(nullable = false,length = 200)
    private String name;

    @Column(nullable = false, length = 200)
    private String email; //one customer can belong to multiple merchants

    @Column(nullable = false, length = 12)
    private String phone;


    private String gstId;

}
