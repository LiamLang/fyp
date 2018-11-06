package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Utils.FileUtils;

public class NodeManager {
    
    public static Node startNodeWithEmptyBlockchain() throws Exception {
        Blockchain bc = new Blockchain(false);
        Node node = new Node(bc);
        node.init();
        return node;
    }
    
    public static Node startNodeWithFirstBlock() throws Exception {
        Blockchain bc = new Blockchain(true);
        Node node = new Node(bc);
        node.init();
        return node;
    }
    
    public static void saveNodeState(Node node, String path) throws Exception {
        FileUtils.saveToFile(node, path);
    }
    
    public static Node getSavedNode(String path) throws Exception {
        Node node = (Node) FileUtils.readFromFile(path);
        node.init();
        return node;
    }
}
