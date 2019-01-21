package com.liamlang.fyp.Model;

import java.io.Serializable;

public class ComponentInfo implements Serializable {
    
    public String info;
    
    public ComponentInfo(String info) {
        this.info = info;
    }
    
    public String toString() {
        return info;
    }
}
