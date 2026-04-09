package com.minibank.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private Integer id;
    private String name;
    private String cpf;
    private Double balance;
    private Boolean active;
}
