package com.minibank.api.mapper;

import com.minibank.api.request.AccountRequest;
import com.minibank.api.response.AccountResponse;
import com.minibank.domain.entitys.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final ModelMapper modelMapper;

    public Account toEntity(AccountRequest request) {
        return modelMapper.map(request, Account.class);
    }

    public AccountResponse toResponse(Account account) {
        return modelMapper.map(account, AccountResponse.class);
    }

    public List<AccountResponse> toResponseList(List<Account> accounts) {
        return accounts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}