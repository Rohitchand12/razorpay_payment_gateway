package com.rohit.razorpay.payment.processor.strategies;

import com.rohit.razorpay.common.utils.RandomizerUtil;
import com.rohit.razorpay.payment.processor.PaymentProcessor;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "40000000000002";
    public static final String PAN_CARD_EXPIRED = "40000000000069";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        String pan = request.pan();

        if(PAN_CARD_DECLINED.equals(pan)){
            log.warn("Card Declined");
            return new PaymentProcessorResponse.failure("CARD_DECLINED","Card declined by bank");
        }
        if(PAN_CARD_EXPIRED.equals(pan)){
            log.warn("Card has expired");
            return new PaymentProcessorResponse.failure("CARD_EXPIRED","Card has expired");
        }

        String processorRef = "CARD_PROCESSOR_"+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.pending(processorRef);
    }
}
