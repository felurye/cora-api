package com.minibank.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Coupon")
class CouponTest {

    @Test
    @DisplayName("Should return true when usage count is below limit")
    void hasAvailableUses_belowLimit() {
        Coupon coupon = Coupon.builder().usageLimit(10).usageCount(9).build();
        assertThat(coupon.hasAvailableUses()).isTrue();
    }

    @Test
    @DisplayName("Should return false when usage count equals limit")
    void hasAvailableUses_atLimit() {
        Coupon coupon = Coupon.builder().usageLimit(10).usageCount(10).build();
        assertThat(coupon.hasAvailableUses()).isFalse();
    }

    @Test
    @DisplayName("Should return false when usage count exceeds limit")
    void hasAvailableUses_aboveLimit() {
        Coupon coupon = Coupon.builder().usageLimit(10).usageCount(11).build();
        assertThat(coupon.hasAvailableUses()).isFalse();
    }

    @Test
    @DisplayName("Should increment usage count by one when use is called")
    void use_incrementsCount() {
        Coupon coupon = Coupon.builder().usageLimit(10).usageCount(0).build();
        coupon.use();
        assertThat(coupon.getUsageCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should accumulate usage count across multiple calls")
    void use_accumulatesCount() {
        Coupon coupon = Coupon.builder().usageLimit(10).usageCount(0).build();
        coupon.use();
        coupon.use();
        coupon.use();
        assertThat(coupon.getUsageCount()).isEqualTo(3);
    }
}
