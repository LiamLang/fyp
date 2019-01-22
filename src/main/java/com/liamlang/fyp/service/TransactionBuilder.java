package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.ComponentInfo;
import com.liamlang.fyp.Model.OwnershipChangeSignature;
import com.liamlang.fyp.Model.Transaction;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class TransactionBuilder {

    Node node;
    
    public TransactionBuilder(Node node) {
        this.node = node;
    }
    
    public Transaction buildNewComponentTransaction(ComponentInfo info, long quantity) {
        
        Component component = new Component(info, new ArrayList<>(), quantity, node.getOwnerName(), node.getKeyPair().getPublic());
        
        ArrayList<Component> components = new ArrayList<>();
        components.add(component);
        
        return new Transaction(components, new ArrayList<>());
    }
    
    public Transaction addComponetsToOther(Component parent, ArrayList<Component> childrenToAdd) throws Exception {
        
        ArrayList<Component> subcomponents = parent.getSubcomponents();
        
        ArrayList<Component> newComponents = new ArrayList<>();

        for (Component child : childrenToAdd) {
            
            if (child.getQuantity() < 1) {
                throw new Exception();
            }
            
            subcomponents.add(child);
            
            Component newChild = new Component(child.getInfo(), child.getSubcomponents(), child.getQuantity() - 1, child.getOwner(), child.getOwnerPubKey());
            
            newComponents.add(newChild);
        }
        
        Component newParent = new Component(parent.getInfo(), subcomponents, parent.getQuantity(), parent.getOwner(), parent.getOwnerPubKey());
                
        newComponents.add(newParent);
        
        return new Transaction(newComponents, new ArrayList<>());
    }
        
    public Transaction removeComponentsFromOther(Component parent, ArrayList<Component> childrenToRemove) throws Exception {
        
        ArrayList<Component> subcomponents = parent.getSubcomponents();
        
        ArrayList<Component> newComponents = new ArrayList<>();
        
        for (Component child : childrenToRemove) {
            
            if (!subcomponents.remove(child)) {
                throw new Exception();
            }
            
            Component newChild = new Component(child.getInfo(), child.getSubcomponents(), child.getQuantity() + 1, child.getOwner(), child.getOwnerPubKey());
            
            newComponents.add(newChild);
        }
        
        Component newParent = new Component(parent.getInfo(), subcomponents, parent.getQuantity(), parent.getOwner(), parent.getOwnerPubKey());
        
        newComponents.add(newParent);
        
        return new Transaction(newComponents, new ArrayList<>());
    }
    
    public Transaction changeOwner(Component component, String newOwner, PublicKey newOwnerPubKey, PrivateKey oldOwnerPrivKey) {
        
        OwnershipChangeSignature signature = new OwnershipChangeSignature(component.getHash(), newOwnerPubKey, oldOwnerPrivKey);
        
        ArrayList<OwnershipChangeSignature> signatures = new ArrayList<>();
        signatures.add(signature);
        
        Component newComponent = new Component(component.getInfo(), component.getSubcomponents(), component.getQuantity(), newOwner, newOwnerPubKey);
        
        ArrayList<Component> components = new ArrayList<>();
        components.add(newComponent);
        
        return new Transaction(components, signatures);
    }
}
