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

        // From the list of input hashes, get a list of the existing unspent components
        ArrayList<Component> oldComponents = new ArrayList<>();

        for (String inputHash : transaction.getInputHashes()) {

            for (Component unspentComponent : node.getUnspentComponents()) {

                if (unspentComponent.getHash().equals(inputHash)) {

                    oldComponents.add(unspentComponent);
                }
            }
        }

        if (!transaction.getOwnershipChangeSignatures().isEmpty()) {

            // This transaction is changing ownership
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

            return true;
        }

        // Check that transaction is the remaining type - involving assembly/disassembly of components
        // Every output must be the same as an input, with either a different quantity remaining, or set of subcomponents
        
        ArrayList<Component> componentsAssembled = new ArrayList<>();
        ArrayList<Component> componentsDisassembled = new ArrayList<>();
        
        ArrayList<Component> subcomponentsAdded = new ArrayList<>();
        ArrayList<Component> subcomponentsRemoved = new ArrayList<>();
        
        for (Component newComponent : transaction.getComponentsCreated()) {
            
            boolean matchFound = false;
            
            for (Component oldComponent : oldComponents) {
                
                if (oldComponent.equalsExceptForQuantity(newComponent)) {
                    
                    matchFound = true;
                    
                    if (oldComponent.getQuantity() > newComponent.getQuantity()) {
                        componentsAssembled.add(oldComponent);
                    } else {
                        componentsDisassembled.add(newComponent);
                    }
                    
                } else if (oldComponent.equalsExceptForSubcomponents(newComponent)) {
                                        
                    for (Component newSubcomponent : newComponent.getSubcomponents())  {
                        
                        boolean subMatchFound = false;
                        
                        for (Component oldSubcomponent : oldComponent.getSubcomponents()) {
                            
                            if (newSubcomponent.equals(oldSubcomponent)) {
                                
                                subMatchFound = true;
                            }
                        }
                        
                        if (!subMatchFound) {
                            
                            subcomponentsAdded.add(newSubcomponent);
                        }
                    }
                    
                    for (Component oldSubcomponent : oldComponent.getSubcomponents()) {
                        
                        boolean subMatchFound = false;
                        
                        for (Component newSubcomponent : newComponent.getSubcomponents()) {
                            
                            if (oldSubcomponent.equals(newSubcomponent)) {
                                
                                subMatchFound = true;
                            }
                        }
                        
                        if (!subMatchFound) {
                            
                            subcomponentsRemoved.add(oldSubcomponent);
                        }
                    }
                    
                    matchFound = true;
                }
            }
            
            if (!matchFound) {                
                return false;
            }
        }
        
        for (Component assembledComponent : componentsAssembled) {
            
            boolean matchFound = false;
            
            for (Component addedSubcomponent : subcomponentsAdded) {
                
                if (assembledComponent.equalsExceptForQuantity(addedSubcomponent)) {
                    
                    matchFound = true;
                }
            }
            
            if (!matchFound) {
                return false;
            }  
        }
        
        for (Component disassembledComponent : componentsDisassembled) {
            
            boolean matchFound = false;
            
            for (Component removedSubcomponent : subcomponentsRemoved) {
                
                if (disassembledComponent.equalsExceptForQuantity(removedSubcomponent)) {
                    
                    matchFound = true;
                }
            }
            
            if (!matchFound) {
                return false;
            }
        }
        
        return true;
    }
}
