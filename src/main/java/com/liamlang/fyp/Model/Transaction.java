package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Transaction implements Serializable, Comparable {

    private final ArrayList<Component> componentsCreated;
    
    // TODO add signatures for transfer of ownership - 
    // either separate to list of components, or as part of each component
    
    // TODO keep the equals and compareTo methods updated
    
    private final long timestamp;

    public Transaction(ArrayList<Component> componentsCreated) {
        
        Collections.sort(componentsCreated);
        
        this.componentsCreated = componentsCreated;
        
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transaction)) {
            return false;
        }
        Transaction other = (Transaction) o;
        return this.componentsCreated.equals(other.componentsCreated) && this.timestamp == other.timestamp;
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
