package com.rohit.razorpay.payment.service.impl;

import com.rohit.razorpay.common.enums.AggregateType;
import com.rohit.razorpay.common.enums.OrderStatus;
import com.rohit.razorpay.common.enums.PaymentEvent;
import com.rohit.razorpay.common.enums.PaymentStatus;
import com.rohit.razorpay.common.exceptions.BusinessRuleViolationException;
import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.payment.Outbox.OutboxEventPublisher;
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
import com.rohit.razorpay.payment.statemachine.PaymentTransitionService;
import com.zaxxer.hikari.util.IsolationLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentTransitionService;
    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    @Transactional
    public PaymentResponseDto initiate(UUID merchantId, PaymentInitRequestDto request) {
        //check if order exists with merchant id and order id
        //PESSIMISTIC LOCK
        OrderRecordEntity order = orderRepository.findByIdAndMerchantIdForUpdate(request.orderId(),merchantId)
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

        //attempted authorize
        paymentTransitionService.apply(paymentEntity,PaymentEvent.AUTHORIZE_ATTEMPT);

        //make a payment initiate call to payment router.
        PaymentResult paymentResult = paymentGatewayRouter.initiate(paymentRequest);
        switch (paymentResult) {
            case PaymentResult.pending pending -> paymentEntity.setProcessorReference(pending.registrationReference());
            case PaymentResult.failure failure -> {
                paymentTransitionService.apply(paymentEntity, PaymentEvent.AUTHORIZE_FAIL);
                paymentEntity.setErrorCode(failure.errCode());
                paymentEntity.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.success success->{
                paymentEntity.setBankReference(success.bankReference());
            }
        }
        paymentEntity = paymentRepository.save(paymentEntity);
        orderRepository.save(order);

        //Create an outbox event for the payment cancellation
        outboxEventPublisher.publish(AggregateType.PAYMENT, paymentEntity.getId(),"PAYMENT_CREATED",
                Map.of(
                        "orderId" ,order.getId().toString(),
                        "paymentId", paymentEntity.getId().toString(),
                        "merchantId",merchantId.toString(),
                        "paymentStatus", paymentEntity.getStatus().name(),
                        "amountUnits",order.getAmount().getAmountUnits(),
                        "amountCurrency",order.getAmount().getCurrency(),
                        "paymentMethod",paymentEntity.getMethod().name()
                )
        );
        return paymentMapper.toPaymentResponseDto(paymentEntity);
    }

    @Override
    @Transactional
    public PaymentResponseDto capture(UUID merchantId, UUID paymentId) {
//        PaymentEntity payment = paymentRepository.findByIdAndMerchantId(paymentId,merchantId)
//                .orElseThrow(()->new ResourceNotFoundException("payment",paymentId));
//
        PaymentEntity paymentEntity = paymentRepository.findByIdAndMerchantIdForUpdate(paymentId,merchantId)
                .orElseThrow(()->new ResourceNotFoundException("payment",paymentId));

        paymentTransitionService.apply(paymentEntity,PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(paymentEntity.getMethod(),paymentId);
        if(paymentResult instanceof  PaymentResult.success success){
            paymentTransitionService.apply(paymentEntity,PaymentEvent.CAPTURE_SUCCESS);
            paymentEntity.setCapturedAt(LocalDateTime.now());
        }else if (paymentResult instanceof  PaymentResult.failure failure) {
            paymentTransitionService.apply(paymentEntity,PaymentEvent.CAPTURE_FAIL);
            paymentEntity.setErrorCode(failure.errCode());
            paymentEntity.setErrorDescription(failure.errorDescription());
        }
        paymentEntity = paymentRepository.save(paymentEntity);

        //Create an outbox event for the payment status change
        outboxEventPublisher.publish(AggregateType.PAYMENT, paymentEntity.getId(),"PAYMENT_STATUS_CHANGED",
                Map.of(
                        "orderId" ,paymentEntity.getId().toString(),
                        "paymentId", paymentEntity.getId().toString(),
                        "merchantId",merchantId.toString(),
                        "paymentStatus", paymentEntity.getStatus().name(),
                        "amountUnits",paymentEntity.getAmount().getAmountUnits(),
                        "amountCurrency",paymentEntity.getAmount().getCurrency(),
                        "paymentMethod",paymentEntity.getMethod().name()
                )
        );
        return paymentMapper.toPaymentResponseDto(paymentEntity);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {
//        PaymentEntity payment = paymentRepository.findById(paymentId)
//                .orElseThrow(()->new ResourceNotFoundException("Payment",paymentId));

        PaymentEntity paymentEntity = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(()->new ResourceNotFoundException("Payment",paymentId));
        if(paymentEntity.getStatus() != PaymentStatus.AUTHORIZING){
            log.warn("Payment is not in authorizing state, payment id : {}, status : {}",paymentId,paymentEntity.getStatus());
            return;
        }
        OrderRecordEntity orderRecord = paymentEntity.getOrder();
        if(approve){
            paymentTransitionService.apply(paymentEntity,PaymentEvent.AUTHORIZE_SUCCESS);
            paymentEntity.setBankReference(bankRef);
            paymentEntity.setAuthorizedAt(LocalDateTime.now());

            //Auto capture
            paymentTransitionService.apply(paymentEntity,PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult =  paymentGatewayRouter.capture(paymentEntity.getMethod(),paymentId);
            switch (captureResult){
                case PaymentResult.success success -> {
                    paymentTransitionService.apply(paymentEntity,PaymentEvent.CAPTURE_SUCCESS);
                    paymentEntity.setCapturedAt(LocalDateTime.now());
                    orderRecord.setStatus(OrderStatus.PAID);
                }
                case PaymentResult.failure failure->{
                    paymentTransitionService.apply(paymentEntity,PaymentEvent.CAPTURE_FAIL);
                    paymentEntity.setErrorCode(failure.errCode());
                    paymentEntity.setErrorDescription(failure.errorDescription());
                }
                case PaymentResult.pending pending ->{
                    //NO PENDING STATE due to synchronous nature, we will immediately get success or fail in capture
                }
            }
        }else{
            paymentTransitionService.apply(paymentEntity,PaymentEvent.AUTHORIZE_FAIL);
            paymentEntity.setErrorDescription(errorDescription);
            paymentEntity.setErrorCode(errorCode);
        }
        paymentRepository.save(paymentEntity);
        orderRepository.save(orderRecord);

        //Create an outbox event for the payment status change
        outboxEventPublisher.publish(AggregateType.PAYMENT, paymentEntity.getId(),"PAYMENT_STATUS_CHANGED",
                Map.of(
                        "orderId" ,paymentEntity.getId().toString(),
                        "paymentId", paymentEntity.getId().toString(),
                        "merchantId",paymentEntity.getMerchantId().toString(),
                        "paymentStatus", paymentEntity.getStatus().name(),
                        "amountUnits",paymentEntity.getAmount().getAmountUnits(),
                        "amountCurrency",paymentEntity.getAmount().getCurrency(),
                        "paymentMethod",paymentEntity.getMethod().name()
                )
        );
    }
}
