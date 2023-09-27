package com.project.micro.bankaccount.mapper;

import com.project.micro.bankaccount.integration.AccountRequest;
import com.project.micro.bankaccount.integration.AccountResponse;
import com.project.micro.bankaccount.model.Account;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


public class AccountMapper {

    public AccountMapper(){

    }

    public static Account toAccountModelReq(AccountRequest accountRequest){

        Account account=new Account();
        BeanUtils.copyProperties(accountRequest,account);
        return account;
    }

    public static AccountResponse toAccountModelRes(Account account){

        AccountResponse accountResponse=new AccountResponse();
        BeanUtils.copyProperties(account,accountResponse);
        return accountResponse;
    }
}
