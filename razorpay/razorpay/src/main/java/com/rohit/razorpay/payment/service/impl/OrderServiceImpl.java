package com.rohit.razorpay.payment.service.impl;

import com.rohit.razorpay.common.enums.OrderStatus;
import com.rohit.razorpay.common.exceptions.BusinessRuleViolationException;
import com.rohit.razorpay.common.exceptions.DuplicateResourceException;
import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.merchant.repository.CustomerRepository;
import com.rohit.razorpay.merchant.service.CustomerService;
import com.rohit.razorpay.payment.dto.request.OrderCreateRequestDto;
import com.rohit.razorpay.payment.dto.response.OrderResponseDto;
import com.rohit.razorpay.payment.dto.response.PaymentResponseDto;
import com.rohit.razorpay.payment.entity.OrderRecordEntity;
import com.rohit.razorpay.payment.entity.PaymentEntity;
import com.rohit.razorpay.payment.mapper.OrderMapper;
import com.rohit.razorpay.payment.mapper.PaymentMapper;
import com.rohit.razorpay.payment.repository.OrderRepository;
import com.rohit.razorpay.payment.repository.PaymentRepository;
import com.rohit.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerService customerService;

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-minutes : 30}")
    private int defaultExpiry;
    @Override
    @Transactional
    public OrderResponseDto create(UUID merchantId, OrderCreateRequestDto request) {
        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId,request.receipt())){
            throw new DuplicateResourceException(
                    "DUPLICATE_ORDER_WITH_RECEIPT"
                    , "Duplicate order found with receipt "+ request.receipt()
            );
        }

        UUID customerId = null;
        if(request.customer() != null){
            customerId = customerService.findOrCreate(
                    merchantId,
                    request.customer().email(),
                    request.customer().name(),
                    request.customer().phone()
            );
        }

        OrderRecordEntity newOrder = OrderRecordEntity.builder()
                .amount(request.amount())
                .receipt(request.receipt())
                .notes(request.notes())
                .status(OrderStatus.CREATED)
                .customerId(customerId)
                .expiresAt(
                        request.expiresAt() != null
                                ? request.expiresAt()
                                : LocalDateTime.now().plusMinutes(defaultExpiry)
                )
                .merchantId(merchantId)//TODO: replace this with UUID coming from api key when merchant makes a call.
                .build();
        newOrder = orderRepository.save(newOrder);
        return orderMapper.toOrderResponseDto(newOrder);
    }

    @Override
    public OrderResponseDto getById(UUID merchantId,UUID id) {
        OrderRecordEntity order =  orderRepository
                .findByIdAndMerchantId(merchantId,id)
                .orElseThrow(()->new ResourceNotFoundException("order",id));

        return orderMapper.toOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto cancel(UUID merchantId, UUID id) {
        OrderRecordEntity order =  orderRepository
                .findByIdAndMerchantId(merchantId,id)
                .orElseThrow(()->new ResourceNotFoundException("order",id));
        if(order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.PAID){
            throw new BusinessRuleViolationException(
                    "CANNOT_CANCEL_ORDER"
                    ,"Cannot cancel order with status  "+ order.getStatus().name()
            );
        }
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        return orderMapper.toOrderResponseDto(order);
    }

    @Override
    public List<PaymentResponseDto> listPayments(UUID merchantId, UUID id) {
         orderRepository
                .findByIdAndMerchantId(merchantId,id)
                .orElseThrow(()->new ResourceNotFoundException("order",id));

        List<PaymentEntity> payments = paymentRepository.findByOrder_Id(merchantId,id);
        return paymentMapper.toPaymentResponseDtoList(payments);
    }
}
