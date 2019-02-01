package com.liamlang.fyp;

import com.liamlang.fyp.Utils.NetworkUtils;
import com.liamlang.fyp.gui.ChangeOwnershipTransactionWindow;
import com.liamlang.fyp.gui.ConnectionsWindow;
import com.liamlang.fyp.gui.MyDetailsWindow;
import com.liamlang.fyp.gui.NewComponentTransactionWindow;
import com.liamlang.fyp.service.Node;
import com.liamlang.fyp.service.NodeManager;
import com.liamlang.fyp.gui.ViewBlockchainWindow;

public class Demo {

    public static void main(String[] args) {
        try {

            Node node = NodeManager.startNodeWithFirstBlock();
            
            /*
            Component child1 = new Component(new ComponentInfo("This is the child component 1"), new ArrayList<>(), 1, "ACME", node.getKeyPair().getPublic());
            Component child2 = new Component(new ComponentInfo("This is the child component 2<br/>BlahBlah<br/>Blah"), new ArrayList<>(), 1, "ACME", node.getKeyPair().getPublic());

            ArrayList<Component> children = new ArrayList<>();
            children.add(child1);
            children.add(child2);
            
            Component parent = new Component(new ComponentInfo("This is the parent component"), children, 1, "ACME", node.getKeyPair().getPublic());
            */
                        
            NewComponentTransactionWindow nctw = new NewComponentTransactionWindow(node);
            nctw.show();
            
            NewComponentTransactionWindow nctw2 = new NewComponentTransactionWindow(node);
            nctw2.show();
            
            NewComponentTransactionWindow nctw3 = new NewComponentTransactionWindow(node);
            nctw3.show();
            
            node.startCreatingBlocks();
            
            ViewBlockchainWindow vbcw = new ViewBlockchainWindow(node.getBlockchain(), node);
            vbcw.show();
            
            //MyDetailsWindow mdw = new MyDetailsWindow(node);
            //mdw.show();
            
            //ConnectionsWindow cw = new ConnectionsWindow(node);
            //cw.show();
            
            //ChangeOwnershipTransactionWindow cotw = new ChangeOwnershipTransactionWindow(node);
            //cotw.show();
            
        } catch (Exception e) {
            System.out.println("Exception caught in Demo");
            e.printStackTrace();
        }
    }
}
