package com.liamlang.fyp;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.ComponentInfo;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.NetworkUtils;
import com.liamlang.fyp.gui.NewComponentTransactionWindow;
import com.liamlang.fyp.gui.ViewBlockWindow;
import com.liamlang.fyp.gui.ViewComponentWindow;
import com.liamlang.fyp.service.Node;
import com.liamlang.fyp.service.NodeManager;
import java.util.ArrayList;

public class Demo {

    public static void main(String[] args) {
        try {

            Node node = NodeManager.startNodeWithFirstBlock();
            
            //Node node = NodeManager.startNodeWithEmptyBlockchain();
            //Node node = NodeManager.getSavedNode("node.txt");
            
            //node.addConnection(NetworkUtils.toIp("192.168.0.227"));
                        
            //node.startCreatingBlocks();
            
            //System.out.println(node.toString());

            Component child1 = new Component(new ComponentInfo("This is the child component 1"), new ArrayList<>(), 1, "ACME", node.getKeyPair().getPublic());
            Component child2 = new Component(new ComponentInfo("This is the child component 2<br/>BlahBlah<br/>Blah"), new ArrayList<>(), 1, "ACME", node.getKeyPair().getPublic());

            ArrayList<Component> children = new ArrayList<>();
            children.add(child1);
            children.add(child2);
            
            Component parent = new Component(new ComponentInfo("This is the parent component"), children, 1, "ACME", node.getKeyPair().getPublic());
            
            //ViewComponentWindow win = new ViewComponentWindow(parent);
            //win.show();
            
            //NewComponentTransactionWindow nctw = new NewComponentTransactionWindow(node);
            //nctw.show();
            
            //node.startCreatingBlocks();
            
        } catch (Exception e) {
            System.out.println("Exception caught in Demo");
        }
    }
}
