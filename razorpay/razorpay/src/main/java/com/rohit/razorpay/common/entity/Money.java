package com.rohit.razorpay.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Money {
    private int amountUnits;
    private String currency;

    public static Money of(int amountUnits, String currency){
        return new Money(amountUnits,currency);
    }

    public static Money inr(int amountUnits){
        return new Money(amountUnits,"INR");
    }

    public Money add(Money other){
        if(!this.currency.equals(other.currency)){
            throw new IllegalArgumentException("Cannot add money with different currencies");
        }
        return new Money(this.amountUnits + other.amountUnits, other.currency);
    }
    public Money subtract(Money other){
        if(!this.currency.equals(other.currency)){
            throw new IllegalArgumentException("Cannot add money with different currencies");
        }
        return new Money(this.amountUnits + other.amountUnits, other.currency);
    }

}
