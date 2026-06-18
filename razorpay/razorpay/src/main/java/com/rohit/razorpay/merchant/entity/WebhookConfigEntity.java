package com.rohit.razorpay.merchant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "webhook_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id",nullable = false)
    private MerchantEntity merchant;

    @Column(length = 500,nullable = false)
    private String targetUrl;

    private String eventTypeFilter; //comma seperated events

    private Boolean enabled;

    private String webhookSecret;


}
