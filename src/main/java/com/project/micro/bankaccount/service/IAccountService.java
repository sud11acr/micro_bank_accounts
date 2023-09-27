package com.project.micro.bankaccount.service;

import com.project.micro.bankaccount.integration.AccountRequest;
import com.project.micro.bankaccount.integration.AccountResponse;
import com.project.micro.bankaccount.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountService {
    Mono<AccountResponse> save(Mono<AccountRequest> account);
    Mono<AccountResponse> update(String id,Mono<AccountRequest> account);
    Flux<AccountResponse> findAll();
    Mono<AccountResponse>findByid(String id);
    Mono<Void> delete(String id);
}
