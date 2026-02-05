package com.bank.bankingapp.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.bankingapp.entity.Account;
import com.bank.bankingapp.exception.AccountNotFoundException;
import com.bank.bankingapp.exception.DuplicateResourceException;
import com.bank.bankingapp.exception.ValidationException;
import com.bank.bankingapp.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repo;

    // Validation patterns
    // private static final Pattern AADHAR_PATTERN = Pattern.compile("^\\d{12}$");
    // private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    // private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,100}$");
    // private static final Pattern CUSTOMER_ID_PATTERN = Pattern.compile("^CUST\\d{3,}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern AADHAR_PATTERN = Pattern.compile("^[2-9]\\d{11}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,100}$");
    private static final Pattern CUSTOMER_ID_PATTERN = Pattern.compile("^CUST\\d{3,}$", Pattern.CASE_INSENSITIVE);

    public Account createAccount(Account acc) {
        // ========== VALIDATION LAYER ==========
        
        // 1. Customer ID validation
        if (acc.getCustomerId() == null || acc.getCustomerId().trim().isEmpty()) {
            throw new ValidationException("Customer ID is required");
        }
        String customerId = acc.getCustomerId().trim().toUpperCase();
        if (!CUSTOMER_ID_PATTERN.matcher(customerId).matches()) {
            throw new ValidationException("Customer ID must be in format CUST followed by numbers (e.g., CUST001)");
        }
        acc.setCustomerId(customerId);
        
        // 2. Customer Name validation
        if (acc.getCustomerName() == null || acc.getCustomerName().trim().isEmpty()) {
            throw new ValidationException("Customer name is required");
        }
        String customerName = acc.getCustomerName().trim();
        if (!NAME_PATTERN.matcher(customerName).matches()) {
            throw new ValidationException("Customer name must contain only letters and spaces (2-100 characters)");
        }
        acc.setCustomerName(customerName);
        
        // 3. Aadhar validation
        if (acc.getAadhar() == null || acc.getAadhar().trim().isEmpty()) {
            throw new ValidationException("Aadhar number is required");
        }
        String aadhar = acc.getAadhar().trim();
        if (!AADHAR_PATTERN.matcher(aadhar).matches()) {
            throw new ValidationException("Aadhar number must be exactly 12 digits");
        }
        // Check for invalid patterns (all same digits)
        if (aadhar.chars().distinct().count() == 1) {
            throw new ValidationException("Invalid Aadhar number: Cannot contain all same digits");
        }
        // Check for sequential patterns
        if (aadhar.equals("123456789012") || aadhar.equals("012345678901")) {
            throw new ValidationException("Invalid Aadhar number: Sequential numbers not allowed");
        }
        acc.setAadhar(aadhar);
        
        // 4. Phone validation
        if (acc.getPhone() == null || acc.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone number is required");
        }
        String phone = acc.getPhone().trim();
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Phone number must be 10 digits starting with 6, 7, 8, or 9");
        }
        // Check for invalid patterns
        if (phone.chars().distinct().count() == 1) {
            throw new ValidationException("Invalid phone number: Cannot contain all same digits");
        }
        acc.setPhone(phone);
        
        // 5. Account Type validation
        if (acc.getAccountType() == null || acc.getAccountType().trim().isEmpty()) {
            throw new ValidationException("Account type is required");
        }
        String accountType = acc.getAccountType().trim();
        if (!accountType.equals("Savings") && !accountType.equals("Current")) {
            throw new ValidationException("Account type must be 'Savings' or 'Current'");
        }
        acc.setAccountType(accountType);
        
        // ========== BUSINESS RULES ==========
        
        // Check if Aadhar is already registered
        if (repo.existsByAadhar(aadhar)) {
            throw new DuplicateResourceException("An account with this Aadhar number already exists");
        }
        
        // Check if Phone is already registered
        if (repo.existsByPhone(phone)) {
            throw new DuplicateResourceException("An account with this phone number already exists");
        }
        
        // Check account type limits per customer
        boolean hasSavings = repo.existsByCustomerIdAndAccountType(customerId, "Savings");
        boolean hasCurrent = repo.existsByCustomerIdAndAccountType(customerId, "Current");
        
        if (accountType.equals("Savings") && hasSavings) {
            throw new DuplicateResourceException("Customer already has a Savings account. Only one Savings account per customer is allowed.");
        }
        
        if (accountType.equals("Current") && hasCurrent) {
            throw new DuplicateResourceException("Customer already has a Current account. Only one Current account per customer is allowed.");
        }
        
        // Set initial balance
        acc.setBalance(0.0);
        
        return repo.save(acc);
    }

    public List<Account> getAllAccounts() {
        return repo.findAll();
    }

    public Double getBalance(Long accNo) {
        if (accNo == null || accNo <= 0) {
            throw new ValidationException("Invalid account number");
        }
        
        Account acc = repo.findById(accNo)
            .orElseThrow(() -> new AccountNotFoundException(accNo));
        return acc.getBalance();
    }
    
    public Account getAccount(Long accNo) {
        if (accNo == null || accNo <= 0) {
            throw new ValidationException("Invalid account number");
        }
        
        return repo.findById(accNo)
            .orElseThrow(() -> new AccountNotFoundException(accNo));
    }
}