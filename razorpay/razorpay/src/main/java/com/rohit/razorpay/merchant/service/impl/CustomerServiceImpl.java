package com.rohit.razorpay.merchant.service.impl;

import com.rohit.razorpay.common.exceptions.ResourceNotFoundException;
import com.rohit.razorpay.merchant.entity.CustomerEntity;
import com.rohit.razorpay.merchant.entity.MerchantEntity;
import com.rohit.razorpay.merchant.repository.CustomerRepository;
import com.rohit.razorpay.merchant.repository.MerchantRepository;
import com.rohit.razorpay.merchant.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public UUID findOrCreate(UUID merchantId, String email, String name, String phone) {
        if(email == null || email.isBlank()){
            return null;
        }
        return customerRepository
                .findByMerchant_IdAndEmail(merchantId,email)
                .map((c)->c.getId())
                .orElseGet(()->createNew(merchantId,email,name,phone));
    }

    public UUID createNew(UUID merchantId, String email, String name, String phone) {
        MerchantEntity merchant = merchantRepository.findById(merchantId)
                .orElseThrow(()->new ResourceNotFoundException("Merchant", merchantId));
        CustomerEntity customer = CustomerEntity.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .build();
        customer = customerRepository.save(customer);
        log.info("Customer saved via findOrCreate id = {}, name = {}, email = {}",customer.getId(), name,email);
        return customer.getId();
    }
}
