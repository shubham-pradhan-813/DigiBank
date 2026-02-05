package com.bank.bankingapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txnId;

    private Long accountNo;
    private String txnType;  // CREDIT or DEBIT
    private Double amount;

    // THIS IS THE ONLY DATE FIELD WE USE NOW
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime txnDate;

    @PrePersist
    public void prePersist() {
        this.txnDate = LocalDateTime.now();
    }

    // Constructors
    public Transaction() {}

    public Transaction(Long accountNo, String txnType, Double amount) {
        this.accountNo = accountNo;
        this.txnType = txnType.toUpperCase(); // Always save uppercase
        this.amount = amount;
    }

    // Getters and Setters
    public Long getTxnId() { return txnId; }
    public void setTxnId(Long txnId) { this.txnId = txnId; }

    public Long getAccountNo() { return accountNo; }
    public void setAccountNo(Long accountNo) { this.accountNo = accountNo; }

    public String getTxnType() { return txnType; }
    public void setTxnType(String txnType) { this.txnType = txnType.toUpperCase(); }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDateTime getTxnDate() { return txnDate; }
    public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }
}