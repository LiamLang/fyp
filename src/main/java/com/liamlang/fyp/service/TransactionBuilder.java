package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.ComponentInfo;
import com.liamlang.fyp.Model.Transaction;
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
    
    public Transaction addComponetsToOther(Component parent, ArrayList<Component> children) {
        
        // get list of subcomponents from parent
        
        // add new children to it
        
        // create new parent
    }
        
    // TODO divide components
    
    // TODO change of ownership - involving signature
    
}
