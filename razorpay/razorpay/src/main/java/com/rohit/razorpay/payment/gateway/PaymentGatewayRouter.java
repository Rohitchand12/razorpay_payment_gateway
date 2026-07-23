package com.rohit.razorpay.payment.gateway;

import com.rohit.razorpay.common.enums.PaymentMethod;
import com.rohit.razorpay.common.exceptions.BusinessRuleViolationException;
import com.rohit.razorpay.payment.gateway.dto.PaymentRequest;
import com.rohit.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {
    private final Map<PaymentMethod, PaymentGatewayAdapter> PaymentAdapterMapper;

    public PaymentResult initiate(PaymentRequest request){
        PaymentGatewayAdapter adapter = PaymentAdapterMapper.get(request.paymentMethod());
        if(adapter == null){
            throw new IllegalArgumentException(
                    "No payment adapter registered for method: "+ request.paymentMethod()
            );
        }
        return adapter.initiate(request);
    }
    public PaymentResult capture(PaymentMethod paymentMethod, UUID paymentId){
        PaymentGatewayAdapter adapter = PaymentAdapterMapper.get(paymentMethod);
        if(adapter == null){
            throw new IllegalArgumentException(
                    "No payment adapter registered for method: "+paymentMethod
            );
        }
        return adapter.capture(paymentId);
    }
}