package com.minibank.domain.repository;

import com.minibank.domain.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    Optional<Coupon> findByCode(String code);
}
