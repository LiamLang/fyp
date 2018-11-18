package com.liamlang.fyp;

import com.liamlang.fyp.Utils.NetworkUtils;
import com.liamlang.fyp.service.Node;
import com.liamlang.fyp.service.NodeManager;

public class Demo {

    public static void main(String[] args) {
        try {

            //Node node = NodeManager.startNodeWithFirstBlock();
            Node node = NodeManager.startNodeWithEmptyBlockchain();
            //Node node = NodeManager.getSavedNode("node.txt");
            
            node.addConnection(NetworkUtils.toIp("192.168.0.249"));
            
            System.out.println(node.toString());
            
        } catch (Exception e) {
            System.out.println("Exception caught in Demo");
        }
    }
}
