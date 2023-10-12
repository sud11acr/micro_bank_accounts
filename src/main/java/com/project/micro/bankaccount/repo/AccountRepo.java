package com.project.micro.bankaccount.repo;

import com.project.micro.bankaccount.model.Account;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Repository
public interface AccountRepo extends ReactiveMongoRepository<Account,String> {
    @Query(value = "{ }, sort = { 'registrationDate' : -1 }, limit = 1")
    Mono<Account> findLatestCustomer();

    Mono<Account> findByIdCustomerAndStatus(String idCustomer,boolean status);

    @Query("{'idCustomer': ?0, 'modificationDate': { $gte: ?1, $lte: ?2 } }")
    Flux<Account> findByIdCustomerAndModificationDateBetween(String idCustomer,Date startDate, Date endDate);


}
