package com.rohit.razorpay.merchant.entity;

import com.rohit.razorpay.common.entity.BaseEntity;
import com.rohit.razorpay.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "app_user", indexes = {
        @Index(name = "idx_app_user_merchant_id",columnList ="merchant_id" )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private MerchantEntity merchant;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password_hash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
}
