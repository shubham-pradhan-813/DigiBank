package com.bank.bankingapp.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
    
    public AccountNotFoundException(Long accountNo) {
        super("Account not found with account number: " + accountNo);
    }
}