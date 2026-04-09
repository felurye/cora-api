package com.minibank.config;

import com.minibank.domain.entities.Coupon;
import com.minibank.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final CouponRepository couponRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (couponRepository.findByCode("CORA10").isEmpty()) {
            couponRepository.save(Coupon.builder()
                    .code("CORA10")
                    .usageLimit(2)
                    .usageCount(0)
                    .bonusAmount(10.0)
                    .build());
        }
    }
}
