package com.project.micro.bankaccount.proxy.service.impl;

import com.project.micro.bankaccount.proxy.bean.CreditCardBean;
import com.project.micro.bankaccount.proxy.service.ICreditCardProxyService;
import com.project.micro.bankaccount.utils.ExternalProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
@Service
public class CreditCardProxyServiceImpl implements ICreditCardProxyService {

    @Autowired
    private ExternalProperties p;

    @Override
    public Mono<CreditCardBean> findByIdCustomer(String idCredit) {
        WebClient webClient= WebClient.builder().baseUrl(p.urlCreditCard).build();
        return webClient.get()
                .uri("/findByIdCustomer/{id}", Collections.singletonMap("id", idCredit))
                .retrieve()
                .bodyToMono(CreditCardBean.class);
    }


}
