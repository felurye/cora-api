package com.minibank.api.controller;

import com.minibank.api.mapper.AccountMapper;
import com.minibank.api.request.AccountRequest;
import com.minibank.api.response.AccountResponse;
import com.minibank.domain.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@CrossOrigin
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper mapper;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody AccountRequest request) {
        accountService.saveAccount(mapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(mapper.toResponseList(accountService.getAll()));
    }
}
