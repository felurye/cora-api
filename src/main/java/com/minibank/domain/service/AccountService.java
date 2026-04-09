package com.minibank.domain.service;

import com.minibank.api.exception.DuplicateCpfException;
import com.minibank.domain.entities.Account;
import com.minibank.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    final String CODE = "CORA10";

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account saveAccount(Account account) {
        if (accountRepository.findByCpf(account.getCpf()).isPresent()) {
            throw new DuplicateCpfException(account.getCpf());
        }
        if (account.getReferralCode() == null) {
            account.setBalance(0.0);
        } else if  (CODE.equals(account.getReferralCode())) {
            account.setBalance(10.0);
        } else {
            throw new IllegalArgumentException("Invalid referral code: " + account.getReferralCode());
        }

        account.setActive(true);
        accountRepository.saveAndFlush(account);

        return account;
    }

    public List<Account> getAll(){
        return accountRepository.findAll();
    }

    public Account getAccount(Integer accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    public Account getAccountByCPF(String cpf) {
        return accountRepository.findByCpf(cpf).orElse(null);
    }

}
