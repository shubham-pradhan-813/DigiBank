package com.bank.bankingapp.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.bankingapp.entity.Customer;
import com.bank.bankingapp.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    public Customer createCustomer(Customer c) {
        c.setCustomerId("CUST" + System.currentTimeMillis());
        c.setcreatedDate(LocalDateTime.now());
        return repo.save(c);
        
    }
}
