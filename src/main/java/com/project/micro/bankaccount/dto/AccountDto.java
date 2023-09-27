package com.project.micro.bankaccount.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AccountDto {

    private String idAccount;
    private String idCustomer;
    private String accountType;
    private String numberAccount;
    private BigDecimal balance;
    private int limitMovement;

}
