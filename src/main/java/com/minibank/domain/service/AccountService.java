package com.minibank.domain.service;

import com.minibank.api.exception.DuplicateCpfException;
import com.minibank.domain.entitys.Account;
import com.minibank.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void saveAccount(Account account) {
        if (accountRepository.findByCpf(account.getCpf()).isPresent()) {
            throw new DuplicateCpfException(account.getCpf());
        }
        accountRepository.saveAndFlush(account);
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
