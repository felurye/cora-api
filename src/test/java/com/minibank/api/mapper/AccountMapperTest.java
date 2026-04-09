package com.minibank.api.mapper;

import com.minibank.api.request.AccountRequest;
import com.minibank.api.response.AccountResponse;
import com.minibank.domain.entities.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccountMapper")
class AccountMapperTest {

    private AccountMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AccountMapper(new ModelMapper());
    }

    @Test
    @DisplayName("Should map AccountRequest to Account")
    void toEntity() {
        AccountRequest request = new AccountRequest("Maria Silva", "12345678901", null);

        Account account = mapper.toEntity(request);

        assertThat(account).isNotNull();
        assertThat(account.getName()).isEqualTo("Maria Silva");
        assertThat(account.getCpf()).isEqualTo("12345678901");
        assertThat(account.getId()).isNull();
    }

    @Test
    @DisplayName("Should map Account to AccountResponse")
    void toResponse() {
        Account account = Account.builder()
                .id(1)
                .name("João Santos")
                .cpf("98765432100")
                .build();

        AccountResponse response = mapper.toResponse(account);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("João Santos");
        assertThat(response.getCpf()).isEqualTo("98765432100");
    }

    @Test
    @DisplayName("Should map a list of Account to a list of AccountResponse")
    void toResponseList() {
        List<Account> accounts = List.of(
                Account.builder().id(1).name("A").cpf("111").build(),
                Account.builder().id(2).name("B").cpf("222").build()
        );

        List<AccountResponse> responses = mapper.toResponseList(accounts);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(1);
        assertThat(responses.get(0).getName()).isEqualTo("A");
        assertThat(responses.get(1).getId()).isEqualTo(2);
        assertThat(responses.get(1).getName()).isEqualTo("B");
    }

    @Test
    @DisplayName("Should return an empty list when mapping an empty list")
    void toResponseListEmpty() {
        List<AccountResponse> responses = mapper.toResponseList(List.of());
        assertThat(responses).isEmpty();
    }
}
