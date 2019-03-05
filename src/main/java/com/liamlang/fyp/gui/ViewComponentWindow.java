package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class ViewComponentWindow {

    private WindowBase window;

    private final Component component;
    private final Node node;

    private boolean isUnspent;
    private Transaction confirmingTx;

    private String confirmationStatus;

    public ViewComponentWindow(Component component, Node node) {
        this.component = component;
        this.node = node;
        this.isUnspent = node.isUnspent(component);
        this.confirmingTx = node.getBlockchain().getTransactionConfirmingComponent(component);
    }

    // This constructor is used for light nodes, which do not maintain a copy of the blockchain
    public ViewComponentWindow(Component component, Node node, String confirmationStatus) {
        this.component = component;
        this.node = node;
        this.isUnspent = false;
        this.confirmingTx = null;
        this.confirmationStatus = confirmationStatus;
    }

    public void show() {

        window = new WindowBase("View Component", 600);
        window.init();

        updateWindow();

        window.show();

        Utils.scheduleRepeatingTask(1000, new Runnable() {
            @Override
            public void run() {

                if (isUnspent != node.isUnspent(component) || confirmingTx != node.getBlockchain().getTransactionConfirmingComponent(component)) {

                    isUnspent = node.isUnspent(component);
                    confirmingTx = node.getBlockchain().getTransactionConfirmingComponent(component);

                    updateWindow();
                    window.refresh();
                }
            }
        });
    }

    public void updateWindow() {

        window.removeAll();

        window.addImage("resources/component.png");

        window.addVerticalSpace(20);

        window.addSelectableTextField("Hash: " + component.getHash());

        window.addVerticalSpace(20);

        if (confirmationStatus != null && !confirmationStatus.equals("")) {

            confirmationStatus.replace("_", " ");

            // Light node, confirmation status is supplied by supernode
            window.addLabel(confirmationStatus);

        } else {

            if (confirmingTx != null) {

                window.addLabel(isUnspent ? "Unspent" : "SPENT");

                window.addVerticalSpace(5);

                JButton viewConfirmingTxButton = new JButton("Confirmed at " + Utils.toHumanReadableTime(confirmingTx.getTimestamp()));

                viewConfirmingTxButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        ViewTransactionWindow vtw = new ViewTransactionWindow(confirmingTx, node);
                        vtw.show();
                    }
                });

                window.add(viewConfirmingTxButton);

            } else {

                window.addLabel("UNCONFIRMED");
            }
        }

        window.addVerticalSpace(20);

        window.addLabel("<html>Information:<br/>" + component.getInfo().toString() + "</html>");

        window.addVerticalSpace(10);

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

        window.addVerticalSpace(10);

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

        window.addVerticalSpace(5);

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

        window.addVerticalSpace(5);

        JButton removeSubcomponentButton = new JButton("Remove a subcomponent...");

        removeSubcomponentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                DisassembleComponentsTransactionWindow dctw = new DisassembleComponentsTransactionWindow(node);
                dctw.show();
                dctw.setParent(component.getHash());
            }
        });

        window.add(removeSubcomponentButton);

        window.addVerticalSpace(20);

        window.addLabel("Owner: " + component.getOwner());

        window.addVerticalSpace(5);

        window.addSelectableTextField("Owner's Public Key Hash: " + Utils.toHexString(HashUtils.sha256(component.getOwnerPubKey().getEncoded())));

        if (component.getOwnerPubKey().equals(node.getDsaKeyPair().getPublic())) {

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
    }
}
