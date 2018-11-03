package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.IOException;
import java.io.Serializable;

public class Block implements Serializable {
    
    private final byte[] hash;  
    private final BlockBody body;
    
    public Block(Block previousBlock, BlockData data) throws IOException {
        
        body = new BlockBody(previousBlock, data);
        
        hash = HashUtils.sha256(body);
    }
    
    public String toString() {
        return Integer.toString(getHeight()) + " " + Utils.bytesToHex(getHash()) + " " + Utils.bytesToHex(getPreviousHash()) + " " + body.toString();
    }
    
    public byte[] getHash() {
        return hash;
    }
    
    public byte[] getPreviousHash() {
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
