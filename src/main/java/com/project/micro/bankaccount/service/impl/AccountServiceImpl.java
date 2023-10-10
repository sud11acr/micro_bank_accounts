package com.project.micro.bankaccount.service.impl;

import com.project.micro.bankaccount.exception.ErrorException;
import com.project.micro.bankaccount.integration.AccountRequest;
import com.project.micro.bankaccount.integration.AccountResponse;
import com.project.micro.bankaccount.mapper.AccountMapper;
import com.project.micro.bankaccount.model.Account;
import com.project.micro.bankaccount.repo.AccountRepo;
import com.project.micro.bankaccount.service.IAccountService;
import com.project.micro.bankaccount.utils.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

        return accountRequest.flatMap(accountReq->{
            return validateAccountCustomerType(accountReq.getIdCustomer(),accountReq.getCustomerType(),accountReq.getAccountType()).
                    flatMap(valid->{
                        if(valid){
                            return Mono.just(accountReq);
                        }else{
                            return Mono.error(new ErrorException("Accion no permitida"));
                        }
                    }).map(p->toAccountModelReq(p))
                    .flatMap(
                            p->{
                                BeanUtils.copyProperties(accountRequest,p);
                                p.setRegistrationDate(new Date());
                                p.setModificationDate(new Date());
                                p.setStatus(true);
                                p.setNumberMovements(0);
                                return repo.save(p);
                            })
                    .map(p->toAccountModelRes(p));
        });

    }

    @Override
    public Mono<AccountResponse> update(String id,Mono<AccountRequest> accountRequest) {
        Mono<Account> monoBody = accountRequest.map(p->
        {
            System.out.println("request "+p.toString());
            return toAccountModelReq(p);
        });
        Mono<Account> monoBD = repo.findById(id);

        return monoBD.zipWith(monoBody,(bd,pl)->{
                    bd.setBalance(pl.getBalance());
                    bd.setModificationDate(new Date());
                    bd.setNumberMovements(pl.getNumberMovements());
                    BeanUtils.copyProperties(bd,pl);

            return bd;
        }).flatMap(p->{
                    System.out.println(" save "+p.toString());
            return repo.save(p);
        })
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

    public Mono<Boolean> validateAccountCustomerType(String idCustomer,String customerType,String accountType){
        return Constants.PERSONAL_CUSTOMER.equals(customerType)?
                repo.findByIdCustomerAndStatus(idCustomer,Constants.ACCOUNT_ACTIVE).
                        map(personal->false).
                        defaultIfEmpty(true):
                Mono.just(Constants.CURRENT_ACCOUNT.equals(accountType));

    }


}
