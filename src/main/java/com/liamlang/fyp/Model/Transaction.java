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
    
    public String toString() {
        return data;
    }
}