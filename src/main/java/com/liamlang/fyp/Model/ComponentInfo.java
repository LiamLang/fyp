package com.liamlang.fyp.Model;

import java.io.Serializable;

public class ComponentInfo implements Serializable {
    
    public String html;
    
    public ComponentInfo(String html) {
        this.html = html;
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (!(o instanceof ComponentInfo)) {
            return false;
        }
        
        ComponentInfo other = (ComponentInfo) o;
        return this.html.equals(other.html);
    }
    
    public String toString() {
        return html;
    }
}
