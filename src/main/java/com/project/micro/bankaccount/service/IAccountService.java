package com.project.micro.bankaccount.service;

import com.project.micro.bankaccount.integration.AccountRequest;
import com.project.micro.bankaccount.integration.AccountResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface IAccountService {
    Mono<AccountResponse> save(Mono<AccountRequest> account);
    Mono<AccountResponse> update(String id,Mono<AccountRequest> account);
    Flux<AccountResponse> findAll();
    Mono<AccountResponse> findById(String id);
    Flux<AccountResponse> findIdCustomerBetweenDate(String idCustomer, Date initial, Date last);
    Mono<Void> delete(String id);
}
