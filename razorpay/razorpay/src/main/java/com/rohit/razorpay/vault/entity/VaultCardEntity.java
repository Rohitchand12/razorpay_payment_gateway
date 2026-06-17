package com.rohit.razorpay.vault.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vault_card")
public class VaultCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private byte[] encryptedPan;

    @Column(nullable = false)
    private byte[] encryptedDek; //string used to encrypt the pan encrypted by master key

    @Column(nullable = false, length = 4)
    private String lastFour;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false,length = 6)
    private String bin; // first 6 digits of a card - bank identification number

    @Column(nullable = false)
    private String expiryMonth;

    @Column(nullable = false)
    private String expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private LocalDateTime deletedAt;
}
