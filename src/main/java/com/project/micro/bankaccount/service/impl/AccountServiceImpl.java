package com.project.micro.bankaccount.service.impl;

import com.project.micro.bankaccount.exception.ErrorException;
import com.project.micro.bankaccount.integration.AccountRequest;
import com.project.micro.bankaccount.integration.AccountResponse;
import com.project.micro.bankaccount.model.Account;
import com.project.micro.bankaccount.proxy.service.ICreditCardProxyService;
import com.project.micro.bankaccount.repo.AccountRepo;
import com.project.micro.bankaccount.service.IAccountService;
import com.project.micro.bankaccount.utils.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ICreditCardProxyService proxy;

    @Override
    public Mono<AccountResponse> save(Mono<AccountRequest> accountRequest) {

        return accountRequest.flatMap(accountReq->{
            return validateAccountCustomerType(accountReq.getIdCustomer(),accountReq.getCustomerType(),accountReq.getAccountType()).
                    flatMap(valid->{
                        if(valid){

                            Mono<AccountRequest> MINIMUN_BALANCE = getAccountRequestMono(accountReq);
                            if (MINIMUN_BALANCE != null) return MINIMUN_BALANCE;

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

    private static Mono<AccountRequest> getAccountRequestMono(AccountRequest accountReq) {
        if(accountReq.getAccountType().equals(Constants.PERSONAL_CUSTOMER_ACCOUNT_VIP)&&(accountReq.getBalance().compareTo(Constants.MINIMUN_BALANCE)==-1)){
            return Mono.error(new ErrorException("Accion no permitida valor debe ser mayor o igual a: " + Constants.MINIMUN_BALANCE));
        }
        return null;
    }

    @Override
    public Mono<AccountResponse> update(String id,Mono<AccountRequest> accountRequest) {
        Mono<Account> monoBody = accountRequest.map(p->
        {
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
    public Mono<AccountResponse> findById(String id) {
        return repo.findById(id).map(p->toAccountModelRes(p));
    }

    @Override
    public Flux<AccountResponse> findIdCustomerBetweenDate(String idCustomer, Date initial, Date last) {
        return repo.findByIdCustomerAndModificationDateBetween(idCustomer,initial,last).map(p->toAccountModelRes(p));
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
                repo.findByIdCustomerAndStatus(idCustomer,Constants.ACCOUNT_ACTIVE)
                        .map(personal->false)
                        .defaultIfEmpty(true)
                        .flatMap(valid->{
                            if(valid&&accountType.equals(Constants.PERSONAL_CUSTOMER_ACCOUNT_VIP)){
                                return validatedCreditCardCustomer(idCustomer);
                            }else {
                                return Mono.just(valid);
                            }

                        }): validatedAccountBusinessCustomer(idCustomer, accountType);


    }

    private Mono<Boolean> validatedAccountBusinessCustomer(String idCustomer, String accountType) {
        return Constants.BUSINESS_CUSTOMER_ACCOUNT_PYME.equals(accountType) ?
                validatedCreditCardCustomer(idCustomer) :
                Mono.just(Constants.CURRENT_ACCOUNT.equals(accountType));
    }

    private Mono<Boolean> validatedCreditCardCustomer(String idCustomer) {
        return proxy.findByIdCustomer(idCustomer)
                .map(credit -> true)
                .defaultIfEmpty(false);
    }


}
