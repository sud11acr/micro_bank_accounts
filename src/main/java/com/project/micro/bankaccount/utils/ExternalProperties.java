package com.project.micro.bankaccount.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExternalProperties {

    @Value("${url.credit.card}")
    public String urlCreditCard;
}
