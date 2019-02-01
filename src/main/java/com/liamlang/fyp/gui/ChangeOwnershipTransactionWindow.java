package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Model.TrustedSignee;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;

public class ChangeOwnershipTransactionWindow {

    private final Node node;

    public ChangeOwnershipTransactionWindow(Node node) {
        this.node = node;
    }

    public void show() {

        WindowBase window = new WindowBase("Change Component Ownership (Transaction)", 600);
        window.init();

        window.addImage("src/main/resources/change_ownership.png");

        window.addVerticalSpace(20);

        window.addLabel("Component Hash: ");

        window.addVerticalSpace(10);

        JTextField hashTextField = new JTextField();
        window.add(hashTextField);

        window.addVerticalSpace(20);

        window.addLabel("New Owner's Pubkey Hash: ");

        window.addVerticalSpace(10);

        JTextField pubkeyTextField = new JTextField();
        window.add(pubkeyTextField);

        window.addVerticalSpace(20);

        JButton button = new JButton("Broadcast");

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String hash = hashTextField.getText();
                if (hash.equals("")) {
                    return;
                }

                Component component = node.getUnspentComponent(hash);
                if (component == null) {
                    Utils.showOkPopup("Can't find unspent component with this hash!");
                    return;
                }

                if (!component.getOwnerPubKey().equals(node.getKeyPair().getPublic())) {
                    Utils.showOkPopup("I don't own this component!");
                    return;
                }

                String pubkey = pubkeyTextField.getText();
                if (pubkey.equals("")) {
                    return;
                }

                TrustedSignee newOwner;
                for (TrustedSignee signee : node.getTrustedSignees()) {

                    if (Utils.toHexString(HashUtils.sha256(signee.getPubkey().getEncoded())).equals(pubkey)) {

                        try {
                            
                            Transaction transaction = node.getTransactionBuilder().changeOwner(component, signee.getName(), signee.getPubkey());
                            node.broadcastTransaction(transaction);
                            
                            Utils.showOkPopup("Changed ownership!\n\nNew hash: " + transaction.getComponentsCreated().get(0).getHash());
                            
                            window.close();
                            
                        } catch (Exception ex) {
                            Utils.showOkPopup("Error creating transaction!");
                        }

                        return;
                    }
                }

                Utils.showOkPopup("Unable to find new owner!");
            }
        });

        window.add(button);

        window.show();
    }
}
