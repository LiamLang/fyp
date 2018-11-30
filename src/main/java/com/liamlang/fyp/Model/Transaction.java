package com.liamlang.fyp.Model;

import java.io.Serializable;

public class Transaction implements Serializable {
    
    public String data;
    
    public Transaction() {
        this.data = "";
    }
    
    public Transaction(String data) {
        this.data = data;
    }
    
    public boolean equals(Transaction other) {
        // TODO keep this updated
        return this.data.equals(other.data);
    }
    
    public String toString() {
        return data;
    }
}