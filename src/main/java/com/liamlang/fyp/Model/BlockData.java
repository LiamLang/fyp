package com.liamlang.fyp.Model;

import java.io.Serializable;

public class BlockData implements Serializable {
    
    public String data;
    
    public BlockData() {
        this.data = "";
    }
    
    public BlockData(String data) {
        this.data = data;
    }
    
    public String toString() {
        return data;
    }
}
