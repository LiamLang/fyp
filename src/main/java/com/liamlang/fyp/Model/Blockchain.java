package com.liamlang.fyp.Model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class Blockchain implements Serializable {

    private LinkedList<Block> blocks = new LinkedList<>();

    public Blockchain(boolean createFirstBlock) throws IOException {
        if (createFirstBlock) {
            ArrayList<Transaction> transactions = new ArrayList<>();
            Block firstBlock = new Block(null, new BlockData(transactions));
            addToTop(firstBlock);
        }
    }

    public Block getTop() {
        if (blocks.isEmpty()) {
            return null;
        }
        return blocks.getLast();
    }

    public int getHeight() {
        Block top = getTop();
        if (top == null) {
            return 0;
        }
        return top.getHeight();
    }
    
    public Block getAtHeight(int height) {
        if (height < 1 || height > getHeight()) {
            return null;
        }
        
        return blocks.get(height - 1);
    }

    public boolean addToTop(Block block) {
        if (block == null) {
            return false;
        }

        if (isValidTop(block)) {
            blocks.add(block);
            System.out.println("Added block to my blockchain! " + block.toString());
            return true;
        }

        return false;
    }
    
    public boolean isConfirmed(Transaction t) {
        for (Block b : blocks) {
            if (b.getData().getTransactions().contains(t)) {
                return true;
            }
        }
        return false;
    }
    
    public Block getBlockWithHash(String hash) {
        
        for (Block block : blocks) {
            if (block.getHash().equals(hash)) {
                return block;
            }
        }
        
        return null;
    }
    
    public Transaction getTransactionConfirmingComponent(Component component) {
        
        // Inefficient
        for (Block block : blocks) {
            for (Transaction transaction : block.getData().getTransactions()) {
                for (Component componentCreated : transaction.getComponentsCreated()) {
                    
                    if (componentCreated.getHash().equals(component.getHash()) && component.verifyHash()) {
                        
                        return transaction;
                    }
                }
            }
        }
        
        return null;
    }

    private boolean isValidTop(Block block) {
        
        if (blocks.isEmpty()) {            
            return true;
        }
                
        if (!block.getPreviousHash().equals(getTop().getHash())) {
            return false;
        }
        
        if (block.getTimestamp() < getTop().getTimestamp()) {
            return false;
        }
               
        return true;
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
