package com.rohit.razorpay.payment.config;

import com.rohit.razorpay.common.enums.PaymentMethod;
import com.rohit.razorpay.payment.processor.PaymentProcessor;
import com.rohit.razorpay.payment.processor.strategies.CardPaymentProcessor;
import com.rohit.razorpay.payment.processor.strategies.NetBankingPaymentProcessor;
import com.rohit.razorpay.payment.processor.strategies.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {
    @Bean
    Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.UPI, new UpiPaymentProcessor(),
                PaymentMethod.NET_BANKING, new NetBankingPaymentProcessor()
        );
    }
}
