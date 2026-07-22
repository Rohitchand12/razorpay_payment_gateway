package com.rohit.razorpay.payment.gateway.adapter;

import com.rohit.razorpay.common.enums.PaymentMethod;
import com.rohit.razorpay.payment.gateway.PaymentGatewayAdapter;
import com.rohit.razorpay.payment.gateway.dto.PaymentRequest;
import com.rohit.razorpay.payment.gateway.dto.PaymentResult;
import com.rohit.razorpay.payment.processor.PaymentProcessorRouter;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rohit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NetBankingAdapter implements PaymentGatewayAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        log.info("Initiating net banking with payment id: {}",request.paymentId());
        try{
            PaymentProcessorRequest processorRequest = PaymentProcessorRequest.nonCard(
                    request.paymentId(),
                    request.amount(),
                    PaymentMethod.NET_BANKING,
                    request.methodDetails()
            );
            PaymentProcessorResponse processorResponse =
                    paymentProcessorRouter.charge(processorRequest);

            return switch (processorResponse){
                case PaymentProcessorResponse.failure failure->
                    new PaymentResult.failure(failure.errorCode(),failure.errorDescription());
                case PaymentProcessorResponse.pending pending->
                    new PaymentResult.pending(pending.paymentProcessorReference());
                case PaymentProcessorResponse.success success->
                    new PaymentResult.success(success.bankReference());
            };
        } catch (Exception e) {
            log.warn("Net banking failed, payment id: {}", request.paymentId());
            return new PaymentResult.failure("NTBK_FAILED",e.getMessage());
        }
    }
}
