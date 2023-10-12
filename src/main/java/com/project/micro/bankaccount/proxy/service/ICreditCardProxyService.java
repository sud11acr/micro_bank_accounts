package com.project.micro.bankaccount.proxy.service;

import com.project.micro.bankaccount.proxy.bean.CreditCardBean;
import reactor.core.publisher.Mono;

public interface ICreditCardProxyService {

    Mono<CreditCardBean> findByIdCustomer(String idCredit);
}
