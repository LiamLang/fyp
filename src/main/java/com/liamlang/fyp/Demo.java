package com.liamlang.fyp;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.BlockData;
import com.liamlang.fyp.Model.Blockchain;

public class Demo {

    public static void main(String[] args) {
        try {
            
           Blockchain bc = new Blockchain();
           
           BlockData newData = new BlockData("This is my block");
           Block newBlock = new Block(bc.getTop(), newData);
            
           System.out.println(Boolean.toString(bc.addToTop(newBlock)));
            
           System.out.println(bc.toString());
           
            
        } catch (Exception e) {   
        }
    }    
}
