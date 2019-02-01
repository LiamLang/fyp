package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.OwnershipChangeSignature;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ViewTransactionWindow {

    private final Transaction transaction;

    public ViewTransactionWindow(Transaction transaction) {
        this.transaction = transaction;
    }

    public void show() {

        WindowBase window = new WindowBase("View Transaction", 700);
        window.init();

        window.addImage("src/main/resources/transaction.png");

        window.addVerticalSpace(20);

        window.addLabel("Input Components: " + Integer.toString(transaction.getInputHashes().size()));

        window.addVerticalSpace(10);

        for (String inputHash : transaction.getInputHashes()) {

            window.addSelectableTextField(inputHash);
        }

        window.addVerticalSpace(20);

        window.addLabel("Output Components: " + Integer.toString(transaction.getComponentsCreated().size()));

        window.addVerticalSpace(10);

        for (Component outputComponent : transaction.getComponentsCreated()) {

            JButton button = new JButton(outputComponent.getHash());

            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ViewComponentWindow win = new ViewComponentWindow(outputComponent);
                    win.show();
                }
            });

            window.add(button);
        }

        window.addVerticalSpace(20);

        window.addLabel("Ownership Change Signatures: " + Integer.toString(transaction.getOwnershipChangeSignatures().size()));

        window.addVerticalSpace(10);

        for (OwnershipChangeSignature signature : transaction.getOwnershipChangeSignatures()) {

            window.add(new JLabel("<html>Input component hash: " + signature.getOldComponentHash()
                    + "<br/>New owner's public key hash: " + Utils.toHexString(HashUtils.sha256(signature.getNewOwnerPubKey().getEncoded())) + "</html>"));
        }

        window.addVerticalSpace(20);

        window.addLabel("Timestamp: " + Utils.toHumanReadableTime(transaction.getTimestamp()));

        window.show();
    }
}
