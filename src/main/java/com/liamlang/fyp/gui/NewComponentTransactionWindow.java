package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.ComponentInfo;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        window.addImage("src/main/resources/new_component.png");

        window.addVerticalSpace(20);

        window.addLabel("Information (html): ");

        window.addVerticalSpace(10);

        JTextField infoTextField = new JTextField();
        window.add(infoTextField);

        window.addVerticalSpace(20);

        window.addLabel("Quantity:");

        window.addVerticalSpace(10);

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

                    ComponentInfo componentInfo = new ComponentInfo(info);

                    Transaction transaction = node.getTransactionBuilder().buildNewComponentTransaction(componentInfo, quantity);

                    node.broadcastTransaction(transaction);

                    Utils.showOkPopup("Created component with hash " + transaction.getComponentsCreated().get(0).getHash());

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
