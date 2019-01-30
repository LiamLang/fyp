package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.OwnershipChangeSignature;
import com.liamlang.fyp.Model.Transaction;
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

public class ViewTransactionWindow {

    private final Transaction transaction;

    public ViewTransactionWindow(Transaction transaction) {
        this.transaction = transaction;
    }

    public void show() {

        WindowBase window = new WindowBase("View Transaction");
        JPanel panel = window.getPanel();

        try {
            panel.add(new JLabel(new ImageIcon(ImageIO.read(new File("src/main/resources/transaction.png")))));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Input Components: " + Integer.toString(transaction.getInputHashes().size())));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (String inputHash : transaction.getInputHashes()) {

            panel.add(new JLabel(inputHash));
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Output Components: " + Integer.toString(transaction.getComponentsCreated().size())));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (Component outputComponent : transaction.getComponentsCreated()) {

            JButton button = new JButton(outputComponent.getHash());

            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ViewComponentWindow win = new ViewComponentWindow(outputComponent);
                    win.show();
                }
            });

            panel.add(button);
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Ownership Change Signatures: " + Integer.toString(transaction.getOwnershipChangeSignatures().size())));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (OwnershipChangeSignature signature : transaction.getOwnershipChangeSignatures()) {

            panel.add(new JLabel("<html>Input component hash: " + signature.getOldComponentHash()
                    + "<br/>New owner's public key hash: " + Utils.toHexString(signature.getNewOwnerPubKey().getEncoded()) + "</html>"));
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Timestamp: " + Utils.toHumanReadableTime(transaction.getTimestamp())));

        window.show(700);
    }
}
