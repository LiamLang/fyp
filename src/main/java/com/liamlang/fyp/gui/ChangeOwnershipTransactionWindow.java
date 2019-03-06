package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.OwnershipChangeSignature;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Model.TrustedSignee;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JTextField;

public class ChangeOwnershipTransactionWindow {

    private final Node node;

    private JTextField hashTextField;

    public ChangeOwnershipTransactionWindow(Node node) {
        this.node = node;
    }

    public void setComponentHash(String componentHash) {

        if (hashTextField != null) {
            hashTextField.setText(componentHash);
        }
    }

    public void show() {

        WindowBase window = new WindowBase("Change Component Ownership (Transaction)", 600);
        window.init();

        window.addImage("resources/change_ownership.png");

        window.addVerticalSpace(20);

        window.addLabel("Component Hash: ");

        window.addVerticalSpace(5);

        hashTextField = new JTextField();
        window.add(hashTextField);

        window.addVerticalSpace(20);

        window.addLabel("New Owner's Pubkey Hash: ");

        window.addVerticalSpace(5);

        JTextField pubkeyTextField = new JTextField();
        window.add(pubkeyTextField);

        window.addVerticalSpace(5);

        JButton showListButton = new JButton("(View List)");

        showListButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ConnectionsWindow cw = new ConnectionsWindow(node);
                cw.show();
            }
        });

        window.add(showListButton);

        window.addVerticalSpace(20);

        JButton button = new JButton("Broadcast");

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String hash = hashTextField.getText();
                if (hash.equals("")) {
                    return;
                }

                String pubkey = pubkeyTextField.getText();
                if (pubkey.equals("")) {
                    return;
                }

                TrustedSignee newOwner = null;
                for (TrustedSignee signee : node.getTrustedSignees()) {

                    if (Utils.toHexString(HashUtils.sha256(signee.getPubkey().getEncoded())).equals(pubkey)) {

                        newOwner = signee;
                        break;
                    }
                }

                if (newOwner == null) {
                    Utils.showOkPopup("Unable to find new owner!");
                    return;
                }

                if (node.getNodeType() == Node.NodeType.LIGHTWEIGHT) {

                    Random random = new Random();
                    int supernodeIndex = random.nextInt(node.getConnections().size());

                    OwnershipChangeSignature signature = new OwnershipChangeSignature(hash, newOwner.getPubkey(), node.getDsaKeyPair().getPrivate());

                    node.getPacketSender().sendChangeOwnershipTransactionRequest(node.getConnections().get(supernodeIndex),
                            hash, newOwner.getName(), signature);

                    Utils.showOkPopup("Sent request to supernode at " + node.getConnections().get(supernodeIndex).getIp().toString()
                            + " to create this transaction.");

                    window.close();
                    return;
                }

                Component component = node.getUnspentComponent(hash);
                if (component == null) {
                    Utils.showOkPopup("Can't find unspent component with this hash!");
                    return;
                }

                if (!component.getOwnerPubKey().equals(node.getDsaKeyPair().getPublic())) {
                    Utils.showOkPopup("I don't own this component!");
                    return;
                }

                try {

                    Transaction transaction = node.getTransactionBuilder().changeOwner(component, newOwner.getName(), newOwner.getPubkey());
                    node.broadcastTransaction(transaction);

                    Utils.showOkPopup("Changed ownership!\n\nNew hash: " + transaction.getComponentsCreated().get(0).getHash());

                    ViewComponentWindow vcw = new ViewComponentWindow(transaction.getComponentsCreated().get(0), node);
                    vcw.show();

                    window.close();

                } catch (Exception ex) {
                    Utils.showOkPopup("Error creating transaction!");
                }
            }
        });

        window.add(button);

        window.show();
    }
}
