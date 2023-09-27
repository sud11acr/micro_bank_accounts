package com.project.micro.bankaccount.repo;

import com.project.micro.bankaccount.model.Account;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepo extends ReactiveMongoRepository<Account,String> {
    @Query(value = "{ }, sort = { 'registrationDate' : -1 }, limit = 1")
    Mono<Account> findLatestCustomer();


}
