package com.liamlang.fyp.Model;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class ComponentBody implements Serializable {

    private final ComponentInfo info;
    
    private final ArrayList<Component> subcomponents;
    
    private final String owner;
    private final PublicKey ownerPubKey;
    
    private final long timestamp;
    
    public ComponentBody(ComponentInfo info, ArrayList<Component> subcomponents, String owner, PublicKey ownerPubKey) {
        this.info = info;
        this.subcomponents = subcomponents;
        this.owner = owner;
        this.ownerPubKey = ownerPubKey;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ComponentInfo getInfo() {
        return info;
    }
    
    public ArrayList<Component> getSubcomponents() {
        return subcomponents;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public PublicKey getOwnerPubKey() {
        return ownerPubKey;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}