package com.minibank.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "account")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Integer id;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "name")
    private String name;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "active")
    private  Boolean active;

    @Column(name = "referralCode")
    private String referralCode;
}
