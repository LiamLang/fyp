package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.ComponentInfo;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
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
import javax.swing.JTextField;

public class NewComponentTransactionWindow {

    private final Node node;

    public NewComponentTransactionWindow(Node node) {
        this.node = node;
    }

    public void show() {

        WindowBase window = new WindowBase("Create Component (Transaction)");
        JPanel panel = window.getPanel();

        try {
            panel.add(new JLabel(new ImageIcon(ImageIO.read(new File("src/main/resources/new_component.png")))));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Information (html): "));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextField infoTextField = new JTextField();
        panel.add(infoTextField);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Quantity:"));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextField quantityTextField = new JTextField("1");
        panel.add(quantityTextField);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(new JLabel("Owner: " + node.getOwnerName() + " (me)"));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

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

        panel.add(button);

        window.show(600);
    }
}
