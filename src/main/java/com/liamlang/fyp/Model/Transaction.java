package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;

public class Transaction implements Serializable, Comparable {

    private final ArrayList<String> inputHashes;
    private final ArrayList<Component> componentsCreated;
    private final ArrayList<OwnershipChangeSignature> signatures;
    
    private final long timestamp;

    public Transaction(ArrayList<String> inputHashes, ArrayList<Component> componentsCreated, ArrayList<OwnershipChangeSignature> signatures) {
                
        this.inputHashes = inputHashes;
        this.componentsCreated = componentsCreated;
        this.signatures = signatures;
        
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transaction)) {
            return false;
        }
        Transaction other = (Transaction) o;
        return this.inputHashes.equals(other.inputHashes) && this.componentsCreated.equals(other.componentsCreated) && this.signatures.equals(other.signatures) && this.timestamp == other.timestamp;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Transaction)) {
            return 0;
        }
        Transaction other = (Transaction) o;
        
        return Utils.toString(HashUtils.sha256(Utils.serialize(this.componentsCreated))).compareTo(Utils.toString(HashUtils.sha256(Utils.serialize(other.componentsCreated))));
    }
    
    public ArrayList<String> getInputHashes() {
        return inputHashes;
    }
    
    public ArrayList<Component> getComponentsCreated() {
        return componentsCreated;
    }
    
    public ArrayList<OwnershipChangeSignature> getOwnershipChangeSignatures() {
        return signatures;
    }
}
