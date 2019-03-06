package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JTextField;

public class AssembleComponentsTransactionWindow {

    private final Node node;

    private JTextField parentTextField;
    private JTextField childTextField;

    public AssembleComponentsTransactionWindow(Node node) {
        this.node = node;
    }

    public void setParent(String hash) {

        if (parentTextField != null) {
            parentTextField.setText(hash);
        }
    }

    public void setChild(String hash) {

        if (childTextField != null) {
            childTextField.setText(hash);
        }
    }

    public void show() {

        WindowBase window = new WindowBase("Assemble Components (Transaction)", 600);
        window.init();

        window.addImage("resources/assembly.png");

        window.addVerticalSpace(20);

        window.addLabel("Parent Component Hash: ");

        window.addVerticalSpace(5);

        parentTextField = new JTextField();
        window.add(parentTextField);

        window.addVerticalSpace(20);

        window.addLabel("Child Component Hash: ");

        window.addVerticalSpace(5);

        childTextField = new JTextField();
        window.add(childTextField);

        window.addVerticalSpace(20);

        JButton button = new JButton("Broadcast");

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String parentHash = parentTextField.getText();
                String childHash = childTextField.getText();

                if (parentHash.equals("") || childHash.equals("")) {
                    return;
                }

                if (node.getNodeType() == Node.NodeType.LIGHTWEIGHT) {

                    Random random = new Random();
                    int supernodeIndex = random.nextInt(node.getConnections().size());

                    node.getPacketSender().sendAssembleComponentsTransactionRequest(node.getConnections().get(supernodeIndex),
                            parentHash, childHash);

                    Utils.showOkPopup("Sent request to supernode at " + node.getConnections().get(supernodeIndex).getIp().toString()
                            + " to create this transaction.");

                    window.close();
                    return;
                }

                Component parent = node.getUnspentComponent(parentHash);
                Component child = node.getUnspentComponent(childHash);

                if (parent == null) {

                    Utils.showOkPopup("Can't find (unspent) parent component!");
                    return;

                } else if (child == null) {

                    Utils.showOkPopup("Can't find (unspent) child component!");
                    return;
                }

                ArrayList<Component> children = new ArrayList<>();
                children.add(child);

                try {

                    Transaction transaction = node.getTransactionBuilder().addComponetsToOther(parent, children);
                    node.broadcastTransaction(transaction);

                    Utils.showOkPopup("Assembled successfully!\n\nNew parent component hash: " + transaction.getComponentsCreated().get(1).getHash());

                    ViewComponentWindow vcw = new ViewComponentWindow(transaction.getComponentsCreated().get(1), node);
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
