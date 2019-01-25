package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class Component implements Serializable, Comparable {
    
    private final String hash;
    private final ComponentBody body;
    
    public Component(ComponentInfo info, ArrayList<Component> subcomponents, long quantity, String owner, PublicKey ownerPubKey) {
        
        ComponentBody body = new ComponentBody(info, subcomponents, quantity, owner, ownerPubKey);
        this.body = body;
        
        hash = Utils.toHexString(HashUtils.sha256(Utils.serialize(body)));
    }
    
    public boolean verifyHash() {
        return hash.equals(Utils.toHexString(HashUtils.sha256(Utils.serialize(body))));
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Component)) {
            return false;
        }
        Component other = (Component) o;
        return other.hash.equals(this.hash);
    }
    
    public boolean equalsExceptForOwnership(Component other) {
        
        return getInfo().equals(other.getInfo()) && getSubcomponents().equals(other.getSubcomponents()) && getQuantity() == other.getQuantity();
    }
    
    public boolean equalsExceptForQuantity(Component other) {
        
        return getInfo().equals(other.getInfo()) && getSubcomponents().equals(other.getSubcomponents()) && getOwner().equals(other.getOwner()) && getOwnerPubKey().equals(other.getOwnerPubKey());
    }
    
    public boolean equalsExceptForSubcomponents(Component other) {
        
        return getInfo().equals(other.getInfo()) && getQuantity() == other.getQuantity() && getOwner().equals(other.getOwner()) && getOwnerPubKey().equals(other.getOwnerPubKey());
    }
    
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Component)) {
            return 0;
        }
        Component other = (Component) o;
        return hash.compareTo(other.getHash());
    }
    
    public String toString() {
        return getInfo().toString() + ", made up of " + Integer.toString(getSubcomponents().size()) + " subcomponents, owned by " + getOwner();
    }
    
    public String getHash() {
        return hash;
    }
    
    public ComponentInfo getInfo() {
        return body.getInfo();
    }
    
    public ArrayList<Component> getSubcomponents() {
        return body.getSubcomponents();
    }
    
    public long getQuantity() {
        return body.getQuantity();
    }
    
    public String getOwner() {
        return body.getOwner();
    }
    
    public PublicKey getOwnerPubKey() {
        return body.getOwnerPubKey();
    }
    
    public long getTimestamp() {
        return body.getTimestamp();
    }
}
