package com.project.micro.bankaccount.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document(collection = "account")
public class Account {

    @Id
    private String idAccount;
    private String idCustomer;
    private String accountType;
    private String numberAccount;
    private BigDecimal balance;
    private int limitMovement;
    private int numberMovements;
    private Date fixedTermAccountDate;
    private Date registrationDate;
    private Date modificationDate;
    private Boolean status;


}
