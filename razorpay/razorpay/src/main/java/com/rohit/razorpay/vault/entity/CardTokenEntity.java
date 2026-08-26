package com.rohit.razorpay.vault.entity;

import com.rohit.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card_token")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardTokenEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true,length = 50)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vault_id")
    private VaultCardEntity vaultCard;

    private UUID customerId;

    @Column(nullable = false)
    private UUID merchantId;

    private LocalDateTime revokedAt;
}
