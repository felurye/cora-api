package com.minibank.domain.service;

import com.minibank.api.exception.CouponLimitReachedException;
import com.minibank.api.exception.CouponNotFoundException;
import com.minibank.api.exception.DuplicateCpfException;
import com.minibank.domain.entities.Account;
import com.minibank.domain.entities.Coupon;
import com.minibank.domain.repository.AccountRepository;
import com.minibank.domain.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Should save account with zero balance when no coupon is provided")
    void saveAccount_noCoupon() {
        Account account = Account.builder().name("Teste").cpf("12345678901").build();
        when(accountRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(accountRepository.saveAndFlush(any())).thenReturn(account);

        Account result = accountService.saveAccount(account, null);

        assertThat(result.getBalance()).isEqualTo(0.0);
        assertThat(result.getActive()).isTrue();
        verify(couponRepository, never()).findByCode(any());
        verify(accountRepository).saveAndFlush(account);
    }

    @Test
    @DisplayName("Should save account with bonus balance when a valid coupon is provided")
    void saveAccount_withValidCoupon() {
        Account account = Account.builder().name("Teste").cpf("12345678901").build();
        Coupon coupon = Coupon.builder().code("CORA10").usageLimit(100).usageCount(0).bonusAmount(10.0).build();

        when(accountRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(couponRepository.findByCode("CORA10")).thenReturn(Optional.of(coupon));
        when(accountRepository.saveAndFlush(any())).thenReturn(account);

        Account result = accountService.saveAccount(account, "CORA10");

        assertThat(result.getBalance()).isEqualTo(10.0);
        assertThat(result.getCoupon()).isEqualTo(coupon);
        assertThat(coupon.getUsageCount()).isEqualTo(1);
        verify(accountRepository).saveAndFlush(account);
    }

    @Test
    @DisplayName("Should throw CouponNotFoundException when coupon code does not exist")
    void saveAccount_couponNotFound() {
        Account account = Account.builder().name("Teste").cpf("12345678901").build();
        when(accountRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(couponRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.saveAccount(account, "INVALID"))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessageContaining("INVALID")
                .hasMessageContaining("not found");

        verify(accountRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should throw CouponLimitReachedException when coupon usage limit is reached")
    void saveAccount_couponLimitReached() {
        Account account = Account.builder().name("Teste").cpf("12345678901").build();
        Coupon coupon = Coupon.builder().code("CORA10").usageLimit(10).usageCount(10).bonusAmount(10.0).build();

        when(accountRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(couponRepository.findByCode("CORA10")).thenReturn(Optional.of(coupon));

        assertThatThrownBy(() -> accountService.saveAccount(account, "CORA10"))
                .isInstanceOf(CouponLimitReachedException.class)
                .hasMessageContaining("CORA10")
                .hasMessageContaining("usage limit");

        verify(accountRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should throw DuplicateCpfException when CPF is already registered")
    void saveAccount_duplicateCpf() {
        Account account = Account.builder().name("Outro").cpf("99999999999").build();
        when(accountRepository.findByCpf("99999999999")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.saveAccount(account, null))
                .isInstanceOf(DuplicateCpfException.class)
                .hasMessageContaining("99999999999")
                .hasMessageContaining("already registered");

        verify(accountRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should return all accounts")
    void getAll() {
        List<Account> accounts = List.of(
                Account.builder().id(1).name("A").cpf("111").build(),
                Account.builder().id(2).name("B").cpf("222").build()
        );
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(1).getName()).isEqualTo("B");
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return an account when ID exists")
    void getAccount_found() {
        Account account = Account.builder().id(1).name("X").cpf("123").build();
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        Account result = accountService.getAccount(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(accountRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should return null when ID does not exist")
    void getAccount_notFound() {
        when(accountRepository.findById(99)).thenReturn(Optional.empty());

        Account result = accountService.getAccount(99);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return an account when CPF exists")
    void getAccountByCPF_found() {
        Account account = Account.builder().id(1).name("X").cpf("123").build();
        when(accountRepository.findByCpf("123")).thenReturn(Optional.of(account));

        Account result = accountService.getAccountByCPF("123");

        assertThat(result).isNotNull();
        assertThat(result.getCpf()).isEqualTo("123");
        verify(accountRepository, times(1)).findByCpf("123");
    }

    @Test
    @DisplayName("Should return null when CPF does not exist")
    void getAccountByCPF_notFound() {
        when(accountRepository.findByCpf("999")).thenReturn(Optional.empty());

        Account result = accountService.getAccountByCPF("999");

        assertThat(result).isNull();
    }
}
