        package com.rohit.razorpay.merchant.dto.request;

        import com.rohit.razorpay.common.enums.BusinessType;
        import jakarta.validation.constraints.Email;
        import jakarta.validation.constraints.NotNull;
        import jakarta.validation.constraints.Size;

        public record MerchantSignupRequestDto(

                @NotNull(message = "Name is required")
                @Size(max = 50, message = "Name should not be more than 50 characters")
                String name,

                @Email(message = "Invalid email")
                @NotNull(message = "Email is required")
                String email,

                @Size(min = 6, message = "Password should be at least 6 characters" )
                @NotNull(message = "Password is required")
                String password,

                @Size(max = 50, message = "Business name should be at max 50 characters")
                String businessName,

                BusinessType businessType
        ) {
        }
