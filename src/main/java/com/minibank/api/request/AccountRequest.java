package com.minibank.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {

    @NotBlank(message = "The name is required.")
    private String name;

    @NotBlank(message = "The cpf is required.")
    private String cpf;

    private String referralCode;
}
