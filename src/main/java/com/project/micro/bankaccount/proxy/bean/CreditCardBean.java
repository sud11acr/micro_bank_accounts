package com.project.micro.bankaccount.proxy.bean;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Generated
public class CreditCardBean {

    private String idCreditCard;
    private String idCustomer;
    private String cardType;
    private String descriptionType;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;

}
