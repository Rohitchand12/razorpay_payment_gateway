package com.rohit.razorpay.merchant.entity;

import com.rohit.razorpay.common.enums.Environment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_key")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id",nullable = false)
    private MerchantEntity merchant;

    @Column(unique = true, nullable = false,length = 50)
    private String keyId;

    @Column(nullable = false,length = 200)
    private String keySecretHash;

    @Column(length = 200)
    private String prevKeySecretHash;

    @Column(length = 200)
    private String webhookSecretHash;

    @Enumerated(EnumType.STRING)
    private Environment environment;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    private LocalDateTime lastUsedAt;

    private LocalDateTime rotatedAt;

    private LocalDateTime gracePeriodExpiresAt;

}
