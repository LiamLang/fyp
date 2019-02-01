package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class ViewComponentWindow {

    private final Component component;

    public ViewComponentWindow(Component component) {
        this.component = component;
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

                    ViewComponentWindow win = new ViewComponentWindow(subcomponent);
                    win.show();
                }
            });

            window.add(button);
        }

        window.addVerticalSpace(20);

        window.addLabel("Owner: " + component.getOwner());

        window.addVerticalSpace(10);

        window.addSelectableTextField("Owner's Public Key Hash: " + Utils.toHexString(HashUtils.sha256(component.getOwnerPubKey().getEncoded())));

        window.addVerticalSpace(20);

        window.addLabel("Timestamp: " + Utils.toHumanReadableTime(component.getTimestamp()));

        window.show();
    }
}
