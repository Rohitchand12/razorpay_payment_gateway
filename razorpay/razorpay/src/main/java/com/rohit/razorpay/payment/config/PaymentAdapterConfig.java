package com.rohit.razorpay.payment.config;

import com.rohit.razorpay.common.enums.PaymentMethod;
import com.rohit.razorpay.payment.gateway.PaymentGatewayAdapter;
import com.rohit.razorpay.payment.gateway.adapter.CardAdapter;
import com.rohit.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.rohit.razorpay.payment.gateway.adapter.UpiAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardAdapter cardAdapter;
    private final UpiAdapter upiAdapter;

    @Bean
    public Map<PaymentMethod, PaymentGatewayAdapter> PaymentAdapterMapper(){
        return Map.of(
                PaymentMethod.CARD, cardAdapter,
                PaymentMethod.UPI, upiAdapter,
                PaymentMethod.NET_BANKING, netBankingAdapter
        );
    }
}
