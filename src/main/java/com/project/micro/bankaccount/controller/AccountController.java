package com.project.micro.bankaccount.controller;

import com.project.micro.bankaccount.integration.AccountRequest;
import com.project.micro.bankaccount.integration.AccountResponse;
import com.project.micro.bankaccount.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    public IAccountService iAccountService;

    @GetMapping("/findAll")
    public Mono<ResponseEntity<Flux<AccountResponse>>> findAll() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(iAccountService.findAll()));
    }

    @GetMapping("/findIdCustomerDateBetween/{idCustomer}/{initial}/{last}")
    public Mono<ResponseEntity<Flux<AccountResponse>>> findDateBetween(@PathVariable("idCustomer") String idCustomer, @PathVariable("initial")  @DateTimeFormat(pattern = "yyyy-MM-dd") Date initial, @PathVariable("last") @DateTimeFormat(pattern = "yyyy-MM-dd") Date last) {

        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(iAccountService.findIdCustomerBetweenDate(idCustomer,initial,last)));
    }

    @GetMapping("/findById/{id}")
    public Mono<ResponseEntity<AccountResponse>> findById(@PathVariable String id) {
        System.out.println("findById "+id);
        return iAccountService.findById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<AccountResponse>>save(@Validated @RequestBody  Mono<AccountRequest> accountRequest){
        return iAccountService.save(accountRequest)
                .map(p -> ResponseEntity.created(URI.create("/create".concat("/").concat(p.getIdAccount())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p));
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<AccountResponse>>update(@PathVariable String id,@RequestBody Mono<AccountRequest> accountRequest ){
        System.out.println("update "+id);
        return iAccountService.update(id,accountRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return iAccountService.delete(id).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }


}
