package com.minibank.api.exception;

import lombok.Getter;

@Getter
public class DuplicateCpfException extends RuntimeException {

    private final String cpf;

    public DuplicateCpfException(String cpf) {
        super("There is already an account registered with the CPF informed: " + cpf);
        this.cpf = cpf;
    }
}