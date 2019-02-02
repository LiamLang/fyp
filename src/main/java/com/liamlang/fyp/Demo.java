package com.liamlang.fyp;

import com.liamlang.fyp.gui.HomeWindow;
import com.liamlang.fyp.service.Node;
import com.liamlang.fyp.service.NodeManager;

public class Demo {

    public static void main(String[] args) {
        try {

            Node node = NodeManager.startNodeWithFirstBlock();
                        
            HomeWindow homeWindow = new HomeWindow(node);
            homeWindow.show();
            
        } catch (Exception e) {
            System.out.println("Exception caught in Demo");
            e.printStackTrace();
        }
    }
}
