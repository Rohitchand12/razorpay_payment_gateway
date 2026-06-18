package com.rohit.razorpay.common.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String errorCode,
        String errorDescription,
        LocalDateTime timestamp,
        List<FieldError> fieldErrors
) {
    //below is a nested record, we can create an object like new ErrorResponse.FieldError
    //Much readable to know that fieldError belongs to error response
    public record FieldError(String field, String message){}

    //Two factory functions to centralize and simplify object creation while hiding repetitive initialization logic.
    public static ErrorResponse of(String errorCode, String errorDescription){
        return new ErrorResponse(errorCode,errorDescription,LocalDateTime.now(),null);
    }
    public static ErrorResponse of(String errorCode, String errorDescription, List<FieldError> fieldErrors){
        return new ErrorResponse(errorCode,errorDescription,LocalDateTime.now(),fieldErrors);
    }
}
