package com.rohit.razorpay.merchant.entity;

import com.rohit.razorpay.common.enums.BusinessType;
import com.rohit.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,length = 200)
    private String name;

    @Column(length = 12)
    private String contactNumber;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 100)
    private String businessName;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 200)
    private String websiteUrl;

    @Column(length = 50,nullable = false)
    @Enumerated(EnumType.STRING)
    
    private MerchantStatus merchantStatus;

    @Column(length = 20)
    private String gstId;

    @Column(length = 20)
    private String panId;

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 20)
    private String settlementBankIfsc;

    @Column(length = 200)
    private String settlementBankAccountHolderName;
}
