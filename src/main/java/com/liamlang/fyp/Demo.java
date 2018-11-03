package com.liamlang.fyp;

import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Utils.NetworkUtils;
import com.liamlang.fyp.service.Node;

public class Demo {

    public static void main(String[] args) {
        try {
            
           Blockchain bc = new Blockchain(true);           
           Node node = new Node(bc);
           
           //Blockchain bc = new Blockchain(false);
           //Node node = new Node(bc);
           //node.addConnection(NetworkUtils.toIp("192.168.0.249"));
            
        } catch (Exception e) {
        }
    }    
}
