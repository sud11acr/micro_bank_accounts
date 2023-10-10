package com.project.micro.bankaccount.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

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
    private int numberMovements;
    private int limitMovement;
    private Date fixedTermAccountDate;

}
