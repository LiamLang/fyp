package com.liamlang.fyp.gui;

import com.liamlang.fyp.service.Node;
import javax.swing.JPanel;

public class NewComponentTransactionWindow {

    private final Node node;
    
    public NewComponentTransactionWindow(Node node) {
        this.node = node;
    }   
    
    public void show() {
        
        WindowBase window = new WindowBase("Create Component (Transaction)");
        JPanel panel = window.getPanel();
        
        // TODO
        
        window.show();
    }
}
