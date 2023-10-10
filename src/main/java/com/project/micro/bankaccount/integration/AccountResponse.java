package com.project.micro.bankaccount.integration;

import com.project.micro.bankaccount.dto.AccountDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse extends AccountDto {

    private int numberMovements;

}
