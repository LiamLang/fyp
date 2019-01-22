package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Transaction;

public class TransactionVerifier {

    private Node node;

    public TransactionVerifier(Node node) {
        this.node = node;
    }
    
    public boolean verify(Transaction transaction) {
        
        // TODO will have to change the existing design to add a list of input hashes to each transaction        
        
        
        return true;
    }
}
