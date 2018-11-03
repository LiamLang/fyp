package com.liamlang.fyp.Model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Blockchain implements Serializable {

    private ArrayList<Block> blocks = new ArrayList<>();

    public Blockchain(boolean createFirstBlock) throws IOException {
        if (createFirstBlock) {
            Block firstBlock = new Block(null, new BlockData("Hello world!"));
            addToTop(firstBlock);
        }
    }

    public Block getTop() {
        if (blocks.isEmpty()) {
            return null;
        }
        return blocks.get(blocks.size() - 1);
    }

    public int getHeight() {
        Block top = getTop();
        if (top == null) {
            return -1;
        }
        return top.getHeight();
    }

    public boolean addToTop(Block block) {
        if (block == null) {
            return false;
        }

        if (blocks.isEmpty() || block.getPreviousHash() == getTop().getHash()) {
            blocks.add(block);
            System.out.println("Added block to my blockchain! " + block.toString());
            return true;
        }

        return false;
    }

    public String toString() {
        String res = "";
        if (!blocks.isEmpty()) {
            for (Block block : blocks) {
                res = res + "\n" + block.toString();
            }
        }
        return res;
    }
}
