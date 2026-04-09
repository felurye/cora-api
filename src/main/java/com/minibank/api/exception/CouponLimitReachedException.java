package com.minibank.api.exception;

public class CouponLimitReachedException extends RuntimeException {

    public CouponLimitReachedException(String code) {
        super("Coupon '" + code + "' has reached its usage limit.");
    }
}
