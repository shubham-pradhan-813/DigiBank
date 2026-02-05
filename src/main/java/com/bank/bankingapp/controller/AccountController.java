package com.bank.bankingapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.bankingapp.entity.Account;
import com.bank.bankingapp.service.AccountService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountService service;

    @PostMapping("/CREATE")
    public Account create(@RequestBody Account a) {
        return service.createAccount(a);
    }

    @GetMapping
    public List<Account> all() {
        return service.getAllAccounts();
    }

    @GetMapping("/{accNo}/balance")
    public Double balance(@PathVariable Long accNo) {
        return service.getBalance(accNo);
    }
}
