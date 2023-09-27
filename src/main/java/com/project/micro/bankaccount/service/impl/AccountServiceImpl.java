package com.project.micro.bankaccount.service.impl;

import com.project.micro.bankaccount.integration.AccountRequest;
import com.project.micro.bankaccount.integration.AccountResponse;
import com.project.micro.bankaccount.mapper.AccountMapper;
import com.project.micro.bankaccount.model.Account;
import com.project.micro.bankaccount.repo.AccountRepo;
import com.project.micro.bankaccount.service.IAccountService;
import lombok.extern.log4j.Log4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.logging.Logger;

import static com.project.micro.bankaccount.mapper.AccountMapper.toAccountModelReq;
import static com.project.micro.bankaccount.mapper.AccountMapper.toAccountModelRes;

@Service
public class AccountServiceImpl implements IAccountService {

    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());

    @Autowired
    private AccountRepo repo;

    @Override
    public Mono<AccountResponse> save(Mono<AccountRequest> accountRequest) {
        return accountRequest.map(p->toAccountModelReq(p))
                .flatMap(
                        p->{
                            p.setRegistrationDate(new Date());
                            p.setModificationDate(new Date());
                            p.setStatus(true);
                            p.setNumberAccount(fillZeros(1,10));
                            return repo.save(p);
                        })
                .map(p->toAccountModelRes(p));
    }

    @Override
    public Mono<AccountResponse> update(String id,Mono<AccountRequest> accountRequest) {
        Mono<Account> monoBody = accountRequest.map(p-> toAccountModelReq(p));
        Mono<Account> monoBD = repo.findById(id);

        return monoBD.zipWith(monoBody,(bd,pl)->{
            BeanUtils.copyProperties(pl,bd);
            bd.setIdAccount(id);
            return bd;
        }).flatMap(p->repo.save(p))
                .map(c->toAccountModelRes(c));
    }

    @Override
    public Flux<AccountResponse> findAll() {
        return repo.findAll().map(p->toAccountModelRes(p));
    }

    @Override
    public Mono<AccountResponse> findByid(String id) {
        return repo.findById(id).map(p->toAccountModelRes(p));
    }

    @Override
    public Mono<Void> delete(String id) {
        return repo.deleteById(id);
    }

    public Mono<Boolean> validateExistRecords(){
        logger.info("Metodo validar registros "+ repo.findLatestCustomer().flatMap(p->{
            logger.info("id "+p.getIdCustomer());
            return Mono.just(p);
        }));
        return repo.findLatestCustomer().hasElement();
    }
    public  String fillZeros(int number, int length) {
        String formato = "%0" + length + "d";
        return String.format(formato, number);
    }


}
