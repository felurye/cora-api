package com.minibank.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "coupon")
@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Version
    private Integer version;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count")
    private Integer usageCount;

    @Column(name = "bonus_amount")
    private Double bonusAmount;

    public boolean hasAvailableUses() {
        return usageCount < usageLimit;
    }

    public void use() {
        this.usageCount++;
    }
}

