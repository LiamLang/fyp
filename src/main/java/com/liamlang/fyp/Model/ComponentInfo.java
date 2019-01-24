package com.liamlang.fyp.Model;

import java.io.Serializable;

public class ComponentInfo implements Serializable {
    
    public String info;
    
    public ComponentInfo(String info) {
        this.info = info;
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (!(o instanceof ComponentInfo)) {
            return false;
        }
        
        ComponentInfo other = (ComponentInfo) o;
        return this.info.equals(other.info);
    }
    
    public String toString() {
        return info;
    }
}
