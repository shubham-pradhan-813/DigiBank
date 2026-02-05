package com.bank.bankingapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "account",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "aadhar"),
        @UniqueConstraint(columnNames = "phone")
    }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountNo;

    private String customerId;
    
    @Column(name = "customerName")  // EXACT DB COLUMN NAME
    private String customerName;
    
    private String aadhar;
    private String phone;
    private String accountType;
    private Double balance = 0.0;

    // Constructors
    public Account() {}

    // Getters and Setters
    public Long getAccountNo() { return accountNo; }
    public void setAccountNo(Long accountNo) { this.accountNo = accountNo; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getAadhar() { return aadhar; }
    public void setAadhar(String aadhar) { this.aadhar = aadhar; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
}