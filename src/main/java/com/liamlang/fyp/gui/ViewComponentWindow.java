package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ViewComponentWindow {

    private final Component component;

    public ViewComponentWindow(Component component) {
        this.component = component;
    }

    public void show() {

        WindowBase window = new WindowBase("View Component");
        JPanel panel = window.getPanel();

        try {
            panel.add(new JLabel(new ImageIcon(ImageIO.read(new File("src/main/resources/component.png")))));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        WindowBase.addSelectableTextField(panel, "Hash: " + component.getHash());

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("<html>Information:<br/><br/>" + component.getInfo().toString() + "</html>"));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Quantity: " + Long.toString(component.getQuantity())));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Subcomponents: " + Integer.toString(component.getSubcomponents().size())));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (Component subcomponent : component.getSubcomponents()) {

            JButton button = new JButton("View Subcomponent: " + subcomponent.getHash());

            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ViewComponentWindow win = new ViewComponentWindow(subcomponent);
                    win.show();
                }
            });

            panel.add(button);
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Owner: " + component.getOwner()));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        WindowBase.addSelectableTextField(panel, "Owner's Public Key Hash: " + Utils.toHexString(HashUtils.sha256(component.getOwnerPubKey().getEncoded())));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Timestamp: " + Utils.toHumanReadableTime(component.getTimestamp())));

        window.show(600);
    }
}
