package com.liamlang.fyp.gui;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;

public class MyDetailsWindow {

    private final Node node;

    public MyDetailsWindow(Node node) {
        this.node = node;
    }

    public void show() {

        WindowBase window = new WindowBase("My Details");
        window.init();

        window.addImage("src/main/resources/person.png");
        
        window.addVerticalSpace(20);
        
        window.addSelectableTextField("My Name: " + node.getOwnerName());
        
        window.addVerticalSpace(20);
        
        window.addSelectableTextField("Hash of my Public Key: " + Utils.toHexString(HashUtils.sha256(node.getKeyPair().getPublic().getEncoded())));
        
        window.addVerticalSpace(20);
        
        window.addSelectableTextField("My Public Key: " + Utils.toHexString(node.getKeyPair().getPublic().getEncoded()));
        
        window.addVerticalSpace(20);
        
        window.addSelectableTextField("My Private Key: " + Utils.toHexString(node.getKeyPair().getPrivate().getEncoded()));

        window.show(700);                
    }
}
