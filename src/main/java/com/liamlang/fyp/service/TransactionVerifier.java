package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.OwnershipChangeSignature;
import com.liamlang.fyp.Model.Transaction;
import java.util.ArrayList;

public class TransactionVerifier {

    private Node node;

    public TransactionVerifier(Node node) {
        this.node = node;
    }
    
    public boolean verify(Transaction transaction) {
        
        if (transaction.getInputHashes().isEmpty()) {
            
            // Transaction consists of newly created components

            for (Component component : transaction.getComponentsCreated()) {
                
                if (!component.verifyHash() || !component.getSubcomponents().isEmpty()) {
                    return false;
                }
            }
            
            for (Component component : transaction.getComponentsCreated()) {
                
                node.getUnspentComponents().add(component);
            }
            
            return true;
        }

        if (!transaction.getOwnershipChangeSignatures().isEmpty()) {
            
            // This transaction is changing ownership
            
            // From the list of input hashes, get a list of the existing unspent components
            ArrayList<Component> oldComponents = new ArrayList<>();
            
            for (String inputHash : transaction.getInputHashes()) {
                
                for (Component unspentComponent : node.getUnspentComponents()) {
                    
                    if (unspentComponent.getHash().equals(inputHash)) {
                        
                        oldComponents.add(unspentComponent);
                    }
                }
            }
            
            // If there isn't one that exactly equals each newly created component, fail

            for (Component newComponent : transaction.getComponentsCreated()) {
                
                boolean matchFound = false;
                
                for (Component oldComponent : oldComponents) {
                    
                    if (oldComponent.equalsExceptForOwnership(newComponent)) {
                        matchFound = true;
                    }
                }
                
                if (!matchFound) {
                    return false;
                }
            }
            
            // There must be a signature with the old hash of each component
            
            for (Component newComponent : transaction.getComponentsCreated()) {
                
                boolean signatureVerified = false;
                
                for (OwnershipChangeSignature signature : transaction.getOwnershipChangeSignatures()) {
                    
                
                }
            }
            
            // The pub key of the new owner must match
            
            // The signature must verify
            
            // Fail if not
            
            // If all pass, remove each old component from the node's unspent component list
            
            // Add each newly created component to the node's unspent component list
        }
        
        // else if transaction is assembly or disassembly
        
        return true;
    }
}
