package com.rohit.razorpay.merchant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
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
