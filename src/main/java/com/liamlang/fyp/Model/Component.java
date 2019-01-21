package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class Component implements Serializable {
    
    private final String hash;
    private final ComponentBody body;
    
    public Component(ComponentInfo info, ArrayList<Component> subcomponents, String owner, PublicKey ownerPubKey) {
        
        ComponentBody body = new ComponentBody(info, subcomponents, owner, ownerPubKey);
        this.body = body;
        
        hash = Utils.toHexString(HashUtils.sha256(Utils.serialize(body)));
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Component)) {
            return false;
        }
        Component other = (Component) o;
        return other.hash.equals(this.hash);
    }
    
    public String toString() {
        return getInfo().toString() + ", made up of " + Integer.toString(getSubcomponents().size()) + " subcomponents, owned by " + getOwner();
    }
    
    public ComponentInfo getInfo() {
        return body.getInfo();
    }
    
    public ArrayList<Component> getSubcomponents() {
        return body.getSubcomponents();
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
