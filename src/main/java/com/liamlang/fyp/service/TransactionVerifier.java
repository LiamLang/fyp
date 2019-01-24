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
                
                boolean inputAndSignatureValid = false;
                
                for (Component oldComponent : oldComponents) {
                    
                    if (oldComponent.equalsExceptForOwnership(newComponent)) {
                     
                        // Input component matched. Now check for a valid signature
                        
                        for (OwnershipChangeSignature signature : transaction.getOwnershipChangeSignatures()) {
                            
                            if (signature.verify(oldComponent, newComponent)) {
                                
                                inputAndSignatureValid = true;
                            }
                        }
                    }
                }
                
                if (!inputAndSignatureValid) {
                    return false;
                }
            }
                        
            // Remove old components from node's unspent component list
            for (Component oldComponent : oldComponents) {
                node.getUnspentComponents().remove(oldComponent);
            }
            
            // Add newly created components to the node's unspent component list
            for (Component newComponent : transaction.getComponentsCreated()) {
                node.getUnspentComponents().add(newComponent);
            }
        }
        
        // else if transaction is assembly or disassembly
        
        return true;
    }
}
