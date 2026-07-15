package com.rohit.razorpay.payment.config;

import com.rohit.razorpay.common.enums.PaymentMethod;
import com.rohit.razorpay.payment.gateway.PaymentGatewayAdapter;
import com.rohit.razorpay.payment.gateway.adapter.CardAdapter;
import com.rohit.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.rohit.razorpay.payment.gateway.adapter.UpiAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {
    @Bean
    public Map<PaymentMethod, PaymentGatewayAdapter> PaymentAdapterMapper(){
        return Map.of(
                PaymentMethod.CARD, new CardAdapter(),
                PaymentMethod.UPI, new UpiAdapter(),
                PaymentMethod.NET_BANKING, new NetBankingAdapter()
        );
    }
}
