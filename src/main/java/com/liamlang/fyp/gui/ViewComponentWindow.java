package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class ViewComponentWindow {

    private final Component component;
    private final Node node;

    public ViewComponentWindow(Component component, Node node) {
        this.component = component;
        this.node = node;
    }

    public void show() {

        WindowBase window = new WindowBase("View Component", 600);
        window.init();

        window.addImage("src/main/resources/component.png");

        window.addVerticalSpace(20);

        window.addSelectableTextField("Hash: " + component.getHash());

        window.addVerticalSpace(20);

        window.addLabel("<html>Information:<br/><br/>" + component.getInfo().toString() + "</html>");

        window.addVerticalSpace(20);

        window.addLabel("Quantity: " + Long.toString(component.getQuantity()));

        window.addVerticalSpace(20);

        window.addLabel("Subcomponents: " + Integer.toString(component.getSubcomponents().size()));

        window.addVerticalSpace(10);

        for (Component subcomponent : component.getSubcomponents()) {

            JButton button = new JButton("View Subcomponent: " + subcomponent.getHash());

            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ViewComponentWindow win = new ViewComponentWindow(subcomponent, node);
                    win.show();
                }
            });

            window.add(button);
        }

        window.addVerticalSpace(20);

        JButton addSubcomponentsButton = new JButton("Add subcomponents...");

        addSubcomponentsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                AssembleComponentsTransactionWindow actw = new AssembleComponentsTransactionWindow(node);
                actw.show();
                actw.setParent(component.getHash());
            }
        });

        window.add(addSubcomponentsButton);

        window.addVerticalSpace(10);

        JButton addAsSubcomponentButtom = new JButton("Add as subcomponent to another...");

        addAsSubcomponentButtom.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                AssembleComponentsTransactionWindow actw = new AssembleComponentsTransactionWindow(node);
                actw.show();
                actw.setChild(component.getHash());
            }
        });

        window.add(addAsSubcomponentButtom);

        window.addVerticalSpace(20);

        window.addLabel("Owner: " + component.getOwner());

        window.addVerticalSpace(10);

        window.addSelectableTextField("Owner's Public Key Hash: " + Utils.toHexString(HashUtils.sha256(component.getOwnerPubKey().getEncoded())));

        if (component.getOwnerPubKey().equals(node.getKeyPair().getPublic())) {

            window.addVerticalSpace(10);

            JButton changeOwnershipButton = new JButton("Change owner");

            changeOwnershipButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ChangeOwnershipTransactionWindow cotw = new ChangeOwnershipTransactionWindow(node);
                    cotw.show();
                    cotw.setComponentHash(component.getHash());
                }
            });

            window.add(changeOwnershipButton);
        }

        window.addVerticalSpace(20);

        window.addLabel("Timestamp: " + Utils.toHumanReadableTime(component.getTimestamp()));

        window.show();
    }
}
