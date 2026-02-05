package com.bank.bankingapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.bankingapp.entity.Transaction;
import com.bank.bankingapp.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin("*")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PostMapping("/credit/{accNo}/{amount}")
    public String credit(
            @PathVariable Long accNo,
            @PathVariable Double amount) {

        return service.credit(accNo, amount);
    }

    @PostMapping("/debit/{accNo}/{amount}")
    public String debit(@PathVariable Long accNo, @PathVariable Double amount) {
        return service.debit(accNo, amount);
    }


    @PostMapping("/transfer/{from}/{to}/{amt}")
    public String transfer(@PathVariable Long from,
                           @PathVariable Long to,
                           @PathVariable Double amt) {
        return service.transfer(from, to, amt);
    }

    @GetMapping("/statement/{accNo}")
    public List<Transaction> statement(@PathVariable Long accNo) {
        return service.statement(accNo);
    }
}