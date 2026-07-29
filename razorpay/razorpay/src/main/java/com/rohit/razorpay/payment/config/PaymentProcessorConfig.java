package com.rohit.razorpay.payment.config;

import com.rohit.razorpay.common.enums.PaymentMethod;
import com.rohit.razorpay.payment.processor.PaymentProcessor;
import com.rohit.razorpay.payment.processor.strategies.CardPaymentProcessor;
import com.rohit.razorpay.payment.processor.strategies.NetBankingPaymentProcessor;
import com.rohit.razorpay.payment.processor.strategies.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;
    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    @Bean
    Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return Map.of(
                PaymentMethod.CARD,cardPaymentProcessor,
                PaymentMethod.UPI, upiPaymentProcessor,
                PaymentMethod.NET_BANKING,netBankingPaymentProcessor
        );
    }
}
