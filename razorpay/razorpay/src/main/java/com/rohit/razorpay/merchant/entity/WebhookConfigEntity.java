package com.rohit.razorpay.merchant.entity;

import com.rohit.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "webhook_config",indexes = {
        @Index(name = "idx_webhook_merchant_id",columnList = "merchant_id, enabled")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookConfigEntity extends BaseEntity {

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
