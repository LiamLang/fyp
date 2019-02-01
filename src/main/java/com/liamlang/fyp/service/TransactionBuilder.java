package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.ComponentInfo;
import com.liamlang.fyp.Model.OwnershipChangeSignature;
import com.liamlang.fyp.Model.Transaction;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class TransactionBuilder implements Serializable {

    Node node;
    
    public TransactionBuilder(Node node) {
        this.node = node;
    }
    
    public Transaction buildNewComponentTransaction(ComponentInfo info, long quantity) {
        
        Component component = new Component(info, new ArrayList<>(), quantity, node.getOwnerName(), node.getKeyPair().getPublic());
        
        ArrayList<Component> components = new ArrayList<>();
        components.add(component);
        
        return new Transaction(new ArrayList<>(), components, new ArrayList<>());
    }
    
    public Transaction addComponetsToOther(Component parent, ArrayList<Component> childrenToAdd) throws Exception {
        
        ArrayList<String> inputHashes = new ArrayList<>();
        
        ArrayList<Component> subcomponents = (ArrayList<Component>) parent.getSubcomponents().clone();
        
        ArrayList<Component> newComponents = new ArrayList<>();

        inputHashes.add(parent.getHash());
        
        for (Component child : childrenToAdd) {
            
            if (child.getQuantity() == 0) {
                throw new Exception();
            }
            
            inputHashes.add(child.getHash());
            
            subcomponents.add(child);
            
            Component newChild = new Component(child.getInfo(), child.getSubcomponents(), child.getQuantity() - 1, child.getOwner(), child.getOwnerPubKey());
            
            newComponents.add(newChild);
        }
        
        Component newParent = new Component(parent.getInfo(), subcomponents, parent.getQuantity(), parent.getOwner(), parent.getOwnerPubKey());
                
        newComponents.add(newParent);
        
        return new Transaction(inputHashes, newComponents, new ArrayList<>());
    }
        
    public Transaction removeComponentsFromOther(Component parent, ArrayList<Component> childrenToRemove) throws Exception {
        
        ArrayList<String> inputHashes = new ArrayList<>();
        
        ArrayList<Component> subcomponents = (ArrayList<Component>) parent.getSubcomponents().clone();
        
        ArrayList<Component> newComponents = new ArrayList<>();
        
        inputHashes.add(parent.getHash());
        
        for (Component child : childrenToRemove) {
            
            if (!subcomponents.remove(child)) {
                throw new Exception();
            }
            
            Component newChild = new Component(child.getInfo(), child.getSubcomponents(), child.getQuantity() + 1, child.getOwner(), child.getOwnerPubKey());
            
            newComponents.add(newChild);
            
            inputHashes.add(child.getHash());
        }
        
        Component newParent = new Component(parent.getInfo(), subcomponents, parent.getQuantity(), parent.getOwner(), parent.getOwnerPubKey());
        
        newComponents.add(newParent);
        
        return new Transaction(inputHashes, newComponents, new ArrayList<>());
    }
    
    public Transaction changeOwner(Component component, String newOwner, PublicKey newOwnerPubKey) throws Exception {
        
        if (!component.getOwnerPubKey().equals(node.getKeyPair().getPublic())) {
            System.out.println("Attempting to change ownership of component I don't own!");
            throw new Exception();
        }
        
        OwnershipChangeSignature signature = new OwnershipChangeSignature(component.getHash(), newOwnerPubKey, node.getKeyPair().getPrivate());
        
        ArrayList<OwnershipChangeSignature> signatures = new ArrayList<>();
        signatures.add(signature);
        
        Component newComponent = new Component(component.getInfo(), component.getSubcomponents(), component.getQuantity(), newOwner, newOwnerPubKey);
        
        ArrayList<Component> components = new ArrayList<>();
        components.add(newComponent);
        
        ArrayList<String> inputHashes = new ArrayList<>();
        inputHashes.add(component.getHash());
        
        return new Transaction(inputHashes, components, signatures);
    }
}
