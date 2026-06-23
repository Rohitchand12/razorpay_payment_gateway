package com.rohit.razorpay.common.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceNotFoundException extends RuntimeException{
    private final String resource;
    private final UUID resourceId;

    public ResourceNotFoundException(String resource, UUID resourceId){
        super(resource + " not found with id: " +  resourceId);
        this.resourceId = resourceId;
        this.resource = resource;
    }
}
