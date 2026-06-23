package com.rohit.razorpay.payment.service.impl;

import com.rohit.razorpay.common.enums.OrderStatus;
import com.rohit.razorpay.common.exceptions.DuplicateResourceException;
import com.rohit.razorpay.payment.dto.request.OrderCreateRequestDto;
import com.rohit.razorpay.payment.dto.response.OrderCreateResponseDto;
import com.rohit.razorpay.payment.entity.OrderRecordEntity;
import com.rohit.razorpay.payment.repository.OrderRepository;
import com.rohit.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Value("${payment.order.default-order-expiry-minutes : 30}")
    private int defaultExpiry;
    @Override
    @Transactional
    public OrderCreateResponseDto create(UUID merchantId,OrderCreateRequestDto request) {
        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId,request.receipt())){
            throw new DuplicateResourceException(
                    "DUPLICATE_ORDER_WITH_RECEIPT"
                    , "Duplicate order found with receipt "+ request.receipt()
            );
        }
        OrderRecordEntity newOrder = OrderRecordEntity.builder()
                .amount(request.amount())
                .receipt(request.receipt())
                .notes(request.notes())
                .status(OrderStatus.CREATED)
                .expiresAt(
                        request.expiresAt() != null
                                ? request.expiresAt()
                                : LocalDateTime.now().plusMinutes(defaultExpiry)
                )
                .merchantId(merchantId)//TODO: replace this with UUID coming from api key when merchant makes a call.
                .build();
        newOrder = orderRepository.save(newOrder);
        return new OrderCreateResponseDto(
                newOrder.getId(),
                newOrder.getMerchantId(),
                newOrder.getAmount(),
                newOrder.getStatus(),
                newOrder.getAttempts(),
                newOrder.getNotes(),
                newOrder.getExpiresAt()
        );
    }
}
