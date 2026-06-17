package com.rohit.razorpay.vault.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "card_token")
public class CardTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true,length = 50)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vault_id")
    private VaultCardEntity vaultCard;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID merchantId;
}
