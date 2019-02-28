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

        WindowBase window = new WindowBase("My Details", 700);
        window.init();

        window.addImage("resources/person.png");

        window.addVerticalSpace(20);

        window.addSelectableTextField("My Name: " + node.getOwnerName());

        window.addVerticalSpace(20);

        window.addSelectableTextField("Hash of my Public Signing Key: " + Utils.toHexString(HashUtils.sha256(node.getDsaKeyPair().getPublic().getEncoded())));

        window.addVerticalSpace(20);

        window.addSelectableTextField("My Public Signing Key: " + Utils.toHexString(node.getDsaKeyPair().getPublic().getEncoded()));

        window.addVerticalSpace(20);

        window.addSelectableTextField("My Private Signing Key: " + Utils.toHexString(node.getDsaKeyPair().getPrivate().getEncoded()));

        window.addVerticalSpace(20);

        window.addSelectableTextField("My Public Encryption Key: " + Utils.toHexString(node.getEcKeyPair().getPublic().getEncoded()));

        window.addVerticalSpace(20);

        window.addSelectableTextField("My Private Encryption Key: " + Utils.toHexString(node.getEcKeyPair().getPrivate().getEncoded()));

        window.show();
    }
}
