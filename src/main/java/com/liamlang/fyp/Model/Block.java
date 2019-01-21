package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.IOException;
import java.io.Serializable;

public class Block implements Serializable {
    
    private final String hash;  
    private final BlockBody body;
    
    public Block(Block previousBlock, BlockData data) throws IOException {
        
        body = new BlockBody(previousBlock, data);
        
        hash = Utils.toHexString(HashUtils.sha256(Utils.serialize(body)));
    }
    
    public String toString() {
        return Integer.toString(getHeight()) + " " + getHash() + " " + body.toString();
    }
    
    public String getHash() {
        return hash;
    }
    
    public String getPreviousHash() {
        return body.getPreviousHash();
    }
        
    public int getHeight() {
        return body.getHeight();
    }
    
    public BlockData getData() {
        return body.getData();
    }
    
    public long getTimestamp() {
        return body.getTimestamp();
    }
}
