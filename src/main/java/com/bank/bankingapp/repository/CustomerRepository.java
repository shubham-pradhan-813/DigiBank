package com.bank.bankingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.bankingapp.entity.Customer;


public interface CustomerRepository extends JpaRepository<Customer, String> {}

