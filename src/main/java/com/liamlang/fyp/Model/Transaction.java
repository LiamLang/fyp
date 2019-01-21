package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Transaction implements Serializable, Comparable {

    private final ArrayList<Component> componentsCreated;
    private final ArrayList<OwnershipChangeSignature> signatures;
    
    private final long timestamp;

    public Transaction(ArrayList<Component> componentsCreated, ArrayList<OwnershipChangeSignature> signatures) {
        
        Collections.sort(componentsCreated);
        
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
        return this.componentsCreated.equals(other.componentsCreated) && this.signatures.equals(other.signatures) && this.timestamp == other.timestamp;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Transaction)) {
            return 0;
        }
        Transaction other = (Transaction) o;
        
        return Utils.toString(HashUtils.sha256(Utils.serialize(this.componentsCreated))).compareTo(Utils.toString(HashUtils.sha256(Utils.serialize(other.componentsCreated))));
    }
}
