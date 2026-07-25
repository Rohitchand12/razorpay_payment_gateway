package com.rohit.razorpay.vault.dto.request;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequestDto(

        @NotBlank(message = "PAN is required")
        @LuhnCheck(message = "Invalid card number")
        @Pattern(regexp = "^[0-9]{13,19}$",message = "Pan must be 13 to 19 characters long")
        String pan,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "Invalid CVV")
        String cvv,

        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Invalid expiry month")
        @Max(value = 12, message = "Invalid expiry year")
        Integer expiryMonth,

        @NotNull(message = "Expiry month is required")
        Integer expiryYear,

        UUID customerId,

        @Size(min = 3, message = "Card holder name must be at least 3 characters")
        String cardHolderName
) {
}
