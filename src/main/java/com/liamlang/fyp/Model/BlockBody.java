package com.liamlang.fyp.Model;

import java.io.Serializable;

public class BlockBody implements Serializable {

    private final String previousHash;
    private final int height;

    private final BlockData data;
    private final long timestamp;

    public BlockBody(Block previousBlock, BlockData data) {

        if (data != null) {
            this.data = data;
        } else {
            this.data = new BlockData();
        }
        timestamp = System.currentTimeMillis();

        if (previousBlock != null) {
            previousHash = previousBlock.getHash();
            height = previousBlock.getHeight() + 1;
        } else {
            previousHash = "";
            height = 1;
        }
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public int getHeight() {
        return height;
    }

    public BlockData getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    public String toString() {
        return data.toString();
    }
}
