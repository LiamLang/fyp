package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.ComponentInfo;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import com.liamlang.fyp.service.Node.NodeType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JTextField;

public class NewComponentTransactionWindow {

    private final Node node;

    public NewComponentTransactionWindow(Node node) {
        this.node = node;
    }

    public void show() {

        WindowBase window = new WindowBase("Create Component (Transaction)", 600);
        window.init();

        window.addImage("resources/new_component.png");

        window.addVerticalSpace(20);

        window.addLabel("Information (html): ");

        window.addVerticalSpace(5);

        JTextField infoTextField = new JTextField();
        window.add(infoTextField);

        window.addVerticalSpace(20);

        window.addLabel("Quantity:");

        window.addVerticalSpace(5);

        JTextField quantityTextField = new JTextField("1");
        window.add(quantityTextField);

        window.addVerticalSpace(20);

        window.addLabel("Owner: " + node.getOwnerName() + " (me)");

        window.addVerticalSpace(20);

        JButton button = new JButton("Broadcast");

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String info = infoTextField.getText();
                String quantityString = quantityTextField.getText();

                try {
                    long quantity = Long.parseLong(quantityString);

                    if (quantity < 1) {
                        throw new Exception();
                    }

                    if (info.equals("")) {
                        throw new Exception();
                    }

                    if (node.getNodeType() == NodeType.LIGHTWEIGHT) {

                        Random random = new Random();
                        int supernodeIndex = random.nextInt(node.getConnections().size());

                        node.getPacketSender().sendCreateComponentTransactionRequest(node.getConnections().get(supernodeIndex),
                                info, quantity, node.getOwnerName(), node.getDsaKeyPair().getPublic());

                        Utils.showOkPopup("Sent request to supernode at " + node.getConnections().get(supernodeIndex).getIp().toString()
                                + " to create this component");

                        window.close();
                        return;
                    }

                    Transaction transaction = node.getTransactionBuilder().buildNewComponentTransaction(info,
                            quantity, node.getOwnerName(), node.getDsaKeyPair().getPublic());

                    node.broadcastTransaction(transaction);

                    Utils.showOkPopup("Created component!\n\nHash: " + transaction.getComponentsCreated().get(0).getHash());

                    ViewComponentWindow vcw = new ViewComponentWindow(transaction.getComponentsCreated().get(0), node);
                    vcw.show();

                    window.close();

                } catch (Exception ex) {
                    Utils.showOkPopup("Error!");
                }
            }
        });

        window.add(button);

        window.show();
    }
}
