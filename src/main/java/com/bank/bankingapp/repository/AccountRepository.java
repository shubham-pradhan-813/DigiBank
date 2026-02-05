package com.bank.bankingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.bankingapp.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAadhar(String aadhar);
    boolean existsByPhone(String phone);
    boolean existsByCustomerIdAndAccountType(String customerId, String accountType);
}


