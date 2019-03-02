package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Utils.FileUtils;
import com.liamlang.fyp.service.Node.NodeType;

public class NodeManager {

    public static Node startNodeWithEmptyBlockchain(NodeType type, String ownerName, String ip, String saveFileName) throws Exception {
        Blockchain bc = new Blockchain(false);
        Node node = new Node(type, bc, ownerName, ip, saveFileName);
        node.init();
        return node;
    }

    public static Node startNodeWithFirstBlock(NodeType type, String ownerName, String ip, String saveFileName) throws Exception {
        Blockchain bc = new Blockchain(true);
        Node node = new Node(type, bc, ownerName, ip, saveFileName);
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
