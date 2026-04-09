package com.minibank.domain.exception;

import lombok.Getter;

@Getter
public class DuplicateCpfException extends RuntimeException {

    private final String cpf;

    public DuplicateCpfException(String cpf) {
        super("An account with CPF '" + cpf + "' is already registered.");
        this.cpf = cpf;
    }
}
