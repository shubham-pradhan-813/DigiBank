package com.bank.bankingapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.bankingapp.entity.Account;
import com.bank.bankingapp.entity.Transaction;
import com.bank.bankingapp.exception.AccountNotFoundException;
import com.bank.bankingapp.exception.InsufficientBalanceException;
import com.bank.bankingapp.exception.ValidationException;
import com.bank.bankingapp.repository.AccountRepository;
import com.bank.bankingapp.repository.TransactionRepository;

@Service
public class TransactionService {

    private static final Double MAX_TRANSACTION_AMOUNT = 10000000.0; // 1 Crore
    private static final Double MIN_TRANSACTION_AMOUNT = 1.0;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository txnRepo;

    @Transactional
    public String credit(Long accNo, Double amount) {
        // Validations
        validateAccountNumber(accNo);
        validateAmount(amount, "Credit");
        
        Account acc = accountRepo.findById(accNo)
            .orElseThrow(() -> new AccountNotFoundException(accNo));

        acc.setBalance(acc.getBalance() + amount);
        accountRepo.save(acc);

        Transaction t = new Transaction(accNo, "CREDIT", amount);
        txnRepo.save(t);

        return String.format("₹%.2f credited successfully. New balance: ₹%.2f", amount, acc.getBalance());
    }

    @Transactional
    public String debit(Long accNo, Double amount) {
        // Validations
        validateAccountNumber(accNo);
        validateAmount(amount, "Debit");

        Account acc = accountRepo.findById(accNo)
            .orElseThrow(() -> new AccountNotFoundException(accNo));

        // Check sufficient balance
        if (acc.getBalance() < amount) {
            throw new InsufficientBalanceException(acc.getBalance(), amount);
        }
        
        // Minimum balance check for Savings accounts
        if (acc.getAccountType().equals("Savings")) {
            Double remainingBalance = acc.getBalance() - amount;
            if (remainingBalance < 500.0) {
                throw new ValidationException(
                    String.format("Cannot withdraw. Savings account must maintain minimum balance of ₹500. " +
                        "Maximum withdrawable amount: ₹%.2f", acc.getBalance() - 500.0)
                );
            }
        }

        acc.setBalance(acc.getBalance() - amount);
        accountRepo.save(acc);

        Transaction t = new Transaction(accNo, "DEBIT", amount);
        txnRepo.save(t);

        return String.format("₹%.2f debited successfully. New balance: ₹%.2f", amount, acc.getBalance());
    }

    @Transactional
    public String transfer(Long fromAccNo, Long toAccNo, Double amount) {
        // Validations
        validateAccountNumber(fromAccNo);
        validateAccountNumber(toAccNo);
        validateAmount(amount, "Transfer");
        
        // Same account check
        if (fromAccNo.equals(toAccNo)) {
            throw new ValidationException("Cannot transfer to the same account");
        }

        Account fromAcc = accountRepo.findById(fromAccNo)
            .orElseThrow(() -> new AccountNotFoundException("Source account not found: " + fromAccNo));
        
        Account toAcc = accountRepo.findById(toAccNo)
            .orElseThrow(() -> new AccountNotFoundException("Destination account not found: " + toAccNo));

        // Check sufficient balance
        if (fromAcc.getBalance() < amount) {
            throw new InsufficientBalanceException(fromAcc.getBalance(), amount);
        }
        
        // Minimum balance check for Savings accounts
        if (fromAcc.getAccountType().equals("Savings")) {
            Double remainingBalance = fromAcc.getBalance() - amount;
            if (remainingBalance < 500.0) {
                throw new ValidationException(
                    String.format("Cannot transfer. Savings account must maintain minimum balance of ₹500. " +
                        "Maximum transferable amount: ₹%.2f", fromAcc.getBalance() - 500.0)
                );
            }
        }

        // Perform transfer
        fromAcc.setBalance(fromAcc.getBalance() - amount);
        toAcc.setBalance(toAcc.getBalance() + amount);

        accountRepo.save(fromAcc);
        accountRepo.save(toAcc);

        // Record transactions
        Transaction debitTxn = new Transaction(fromAccNo, "DEBIT", amount);
        Transaction creditTxn = new Transaction(toAccNo, "CREDIT", amount);
        txnRepo.save(debitTxn);
        txnRepo.save(creditTxn);

        return String.format("₹%.2f transferred successfully from %s to %s", 
            amount, fromAcc.getCustomerName(), toAcc.getCustomerName());
    }

    public List<Transaction> statement(Long accNo) {
        validateAccountNumber(accNo);
        
        // Verify account exists
        if (!accountRepo.existsById(accNo)) {
            throw new AccountNotFoundException(accNo);
        }
        
        return txnRepo.findByAccountNo(accNo);
    }
    
    // ========== VALIDATION HELPERS ==========
    
    private void validateAccountNumber(Long accNo) {
        if (accNo == null) {
            throw new ValidationException("Account number is required");
        }
        if (accNo <= 0) {
            throw new ValidationException("Account number must be positive");
        }
    }
    
    private void validateAmount(Double amount, String operation) {
        if (amount == null) {
            throw new ValidationException(operation + " amount is required");
        }
        if (amount <= 0) {
            throw new ValidationException(operation + " amount must be positive");
        }
        if (amount < MIN_TRANSACTION_AMOUNT) {
            throw new ValidationException(
                String.format("Minimum %s amount is ₹%.2f", operation.toLowerCase(), MIN_TRANSACTION_AMOUNT)
            );
        }
        if (amount > MAX_TRANSACTION_AMOUNT) {
            throw new ValidationException(
                String.format("Maximum %s amount is ₹%.2f (1 Crore)", operation.toLowerCase(), MAX_TRANSACTION_AMOUNT)
            );
        }
        // Check for too many decimal places
        String amountStr = amount.toString();
        if (amountStr.contains(".")) {
            String decimals = amountStr.substring(amountStr.indexOf(".") + 1);
            if (decimals.length() > 2) {
                throw new ValidationException("Amount cannot have more than 2 decimal places");
            }
        }
    }
}