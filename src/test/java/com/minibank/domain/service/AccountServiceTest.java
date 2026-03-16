package com.minibank.domain.service;

import com.minibank.api.exception.DuplicateCpfException;
import com.minibank.domain.entitys.Account;
import com.minibank.domain.repository.AccountRepository;
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

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Should save an account when CPF does not exist")
    void saveAccount() {
        Account account = Account.builder()
                .name("Teste")
                .cpf("12345678901")
                .build();
        when(accountRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(accountRepository.saveAndFlush(any(Account.class))).thenReturn(account);

        accountService.saveAccount(account);

        verify(accountRepository).findByCpf("12345678901");
        verify(accountRepository).saveAndFlush(account);
    }

    @Test
    @DisplayName("Should throw DuplicateCpfException when CPF is already registered")
    void saveAccount_duplicateCpf_throws() {
        Account account = Account.builder()
                .name("Outro")
                .cpf("99999999999")
                .build();
        when(accountRepository.findByCpf("99999999999")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.saveAccount(account))
                .isInstanceOf(DuplicateCpfException.class)
                .hasMessageContaining("99999999999");
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
