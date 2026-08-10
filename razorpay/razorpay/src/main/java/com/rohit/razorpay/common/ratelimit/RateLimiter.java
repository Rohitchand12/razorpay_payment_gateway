package com.rohit.razorpay.common.ratelimit;

public interface RateLimiter {
    RateLimitResult check(String key, int maxRequestsAllowed, int windowSeconds);
}
