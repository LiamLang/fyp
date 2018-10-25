package com.liamlang.fyp.Model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Blockchain implements Serializable {
    
    private ArrayList<Block> blocks = new ArrayList<>();
    
    public Blockchain() throws IOException {
        
        Block firstBlock = new Block(null, new BlockData("Hello world!"));
        blocks.add(firstBlock);
    }
    
    public Block getTop() {
        return blocks.get(blocks.size() - 1);
    }
    
    public boolean addToTop(Block block) {
        if (block == null) {
            return false;
        }
        
        if (block.getPreviousHash() == getTop().getHash()) {
            blocks.add(block);
            return true;
        }
        
        return false;
    }
    
    public String toString() {
        String res = "";
        for (Block block : blocks) {
            res = res + "\n" + block.toString();
        }
        return res;
    }
}
