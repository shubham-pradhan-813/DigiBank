package com.bank.bankingapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Customer {
    @Id
    private String customerId;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdDate;

     // ---- GETTERS ----
     public String getCustomerId() {
        return customerId;
    }

    public LocalDateTime getcreatedDate() {
        return createdDate;
    }



    // ---- SETTERS ----
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setcreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

}
