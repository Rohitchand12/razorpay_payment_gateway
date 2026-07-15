package com.rohit.razorpay.payment.service.impl;

import com.rohit.razorpay.common.enums.OrderStatus;
import com.rohit.razorpay.common.enums.PaymentStatus;
import com.rohit.razorpay.common.exceptions.BusinessRuleViolationException;
import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.payment.dto.request.PaymentInitRequestDto;
import com.rohit.razorpay.payment.dto.response.PaymentResponseDto;
import com.rohit.razorpay.payment.entity.OrderRecordEntity;
import com.rohit.razorpay.payment.entity.PaymentEntity;
import com.rohit.razorpay.payment.gateway.PaymentGatewayRouter;
import com.rohit.razorpay.payment.gateway.dto.PaymentRequest;
import com.rohit.razorpay.payment.gateway.dto.PaymentResult;
import com.rohit.razorpay.payment.mapper.PaymentMapper;
import com.rohit.razorpay.payment.repository.OrderRepository;
import com.rohit.razorpay.payment.repository.PaymentRepository;
import com.rohit.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponseDto initiate(UUID merchantId, PaymentInitRequestDto request) {
        //check if order exists with merchant id and order id
        OrderRecordEntity order = orderRepository.findByIdAndMerchantId(merchantId,request.orderId())
                .orElseThrow(()-> new ResourceNotFoundException(
                        "order", request.orderId())
                );
        //check the order status
        if(order.getStatus() != OrderStatus.ATTEMPTED && order.getStatus() != OrderStatus.CREATED){
            throw new BusinessRuleViolationException(
                    "INVALID_ORDER_STATUS"
                    ,"Cannot initiate payment with order status: "+ request.orderId()
            );
        }
        //Change the order status and attempts

        order.setAttempts(order.getAttempts()+1);
        order.setStatus(OrderStatus.ATTEMPTED);

        //make a payment object
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .amount(order.getAmount())
                .merchantId(merchantId)
                .method(request.paymentMethod())
                .methodDetails(request.methodDetails())
                .status(PaymentStatus.CREATED)
                .order(order)
                .build();

        paymentEntity = paymentRepository.save(paymentEntity);

        //create a new payment request
        PaymentRequest paymentRequest = new PaymentRequest(
               paymentEntity.getId(),
               merchantId,
               request.orderId(),
                order.getAmount(),
                request.paymentMethod(),
                request.methodDetails()
        );
        //make a payment initiate call to payment router.
        PaymentResult paymentResult = paymentGatewayRouter.initiate(paymentRequest);
        switch (paymentResult) {
            case PaymentResult.pending pending -> paymentEntity.setProcessorReference(pending.registrationReference());
            case PaymentResult.failure failure -> {
                paymentEntity.setStatus(PaymentStatus.FAILED);
                paymentEntity.setErrorCode(failure.errCode());
                paymentEntity.setErrorDescription(failure.errorDescription());
            }
        }
        paymentEntity = paymentRepository.save(paymentEntity);
        orderRepository.save(order);
        return paymentMapper.toPaymentResponseDto(paymentEntity);
    }
}
