package com.minibank.domain.service;

import com.minibank.api.exception.CouponLimitReachedException;
import com.minibank.api.exception.CouponNotFoundException;
import com.minibank.api.exception.DuplicateCpfException;
import com.minibank.domain.entities.Account;
import com.minibank.domain.entities.Coupon;
import com.minibank.domain.repository.AccountRepository;
import com.minibank.domain.repository.CouponRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CouponRepository couponRepository;

    public AccountService(AccountRepository accountRepository, CouponRepository couponRepository) {
        this.accountRepository = accountRepository;
        this.couponRepository = couponRepository;
    }

    @Transactional
    public Account saveAccount(Account account, String referralCode) {
        if (accountRepository.findByCpf(account.getCpf()).isPresent()) {
            throw new DuplicateCpfException(account.getCpf());
        }
        applyCoupon(account, referralCode);
        account.setActive(true);
        accountRepository.saveAndFlush(account);
        return account;
    }

    private void applyCoupon(Account account, String referralCode) {
        if (referralCode == null) {
            account.setBalance(0.0);
            return;
        }
        Coupon coupon = couponRepository.findByCode(referralCode)
                .orElseThrow(() -> new CouponNotFoundException(referralCode));

        if (!coupon.hasAvailableUses()) {
            throw new CouponLimitReachedException(referralCode);
        }
        coupon.use();
        account.setBalance(coupon.getBonusAmount());
        account.setCoupon(coupon);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getAccount(Integer accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    public Account getAccountByCPF(String cpf) {
        return accountRepository.findByCpf(cpf).orElse(null);
    }
}
