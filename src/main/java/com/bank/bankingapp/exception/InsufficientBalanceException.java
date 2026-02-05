package com.bank.bankingapp.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(Double available, Double requested) {
        super(String.format("Insufficient balance. Available: ₹%.2f, Requested: ₹%.2f", available, requested));
    }
}