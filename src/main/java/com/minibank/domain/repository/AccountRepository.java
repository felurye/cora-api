package com.minibank.domain.repository;

import com.minibank.domain.entitys.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository  extends JpaRepository<Account, Integer> {
    Optional<Account> findByCpf(String cpf);
}
