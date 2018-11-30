package com.liamlang.fyp.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class BlockData implements Serializable {
    
    private ArrayList<Transaction> transactions = new ArrayList<>();
    
    public BlockData() {
    }
    
    public BlockData(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
    
    public String toString() {
        return "Block containing " + Integer.toString(transactions.size()) + " transactions";
    }
}
