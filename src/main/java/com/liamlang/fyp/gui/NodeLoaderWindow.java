package com.liamlang.fyp.gui;

import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.gui.ChooseMyIpWindow.ChooseIpCallback;
import com.liamlang.fyp.service.Node;
import com.liamlang.fyp.service.Node.NodeType;
import com.liamlang.fyp.service.NodeManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class NodeLoaderWindow {

    JRadioButton normalRadioButton = new JRadioButton("Normal (full node)");
    JRadioButton lightRadioButton = new JRadioButton("Lightweight node");
    JRadioButton superRadioButton = new JRadioButton("Supernode");

    public NodeLoaderWindow() {
    }

    public void show() {

        WindowBase window = new WindowBase("Entry Point", 500);
        window.init();

        window.addImage("resources/folder.png");

        window.addVerticalSpace(20);

        window.addLabel("Load Node from File:");

        window.addVerticalSpace(5);

        JTextField loadFileTextField = new JTextField();
        window.add(loadFileTextField);

        loadFileTextField.setText("node.txt");

        window.addVerticalSpace(5);

        JButton loadButton = new JButton("Load");

        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    Node node = NodeManager.getSavedNode(loadFileTextField.getText());

                    HomeWindow homeWindow = new HomeWindow(node);
                    homeWindow.show();

                    window.close();

                } catch (Exception ex) {

                    Utils.showOkPopup("Error loading node:\n\n" + ex.getMessage());
                }
            }
        });

        window.add(loadButton);

        window.addVerticalSpace(20);

        window.addLabel("Create new Node:");

        window.addVerticalSpace(10);

        window.addLabel("Owner name:");

        JTextField ownerNameTextField = new JTextField();

        window.add(ownerNameTextField);

        window.addVerticalSpace(10);

        window.addLabel("Save to path:");

        JTextField savePathTextField = new JTextField();

        window.add(savePathTextField);

        savePathTextField.setText("node.txt");

        window.addVerticalSpace(10);

        ButtonGroup group = new ButtonGroup();

        group.add(normalRadioButton);
        group.add(lightRadioButton);
        group.add(superRadioButton);

        window.add(normalRadioButton);
        window.add(lightRadioButton);
        window.add(superRadioButton);

        normalRadioButton.setSelected(true);

        JButton createNodeButton = new JButton("Create new Node for existing Blockchain");

        createNodeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (ownerNameTextField.getText().equals("") || savePathTextField.getText().equals("")) {
                    return;
                }

                try {

                    ChooseMyIpWindow chooseMyIpWindow = new ChooseMyIpWindow(new ChooseIpCallback() {

                        public void callback(String ip) {

                            try {
                                Node node = NodeManager.startNodeWithEmptyBlockchain(getSelectedType(), ownerNameTextField.getText(), ip, savePathTextField.getText());

                                HomeWindow homeWindow = new HomeWindow(node);
                                homeWindow.show();

                                window.close();

                            } catch (Exception ex) {
                                Utils.showOkPopup("Error creating node:\n\n" + ex.getMessage());
                            }
                        }
                    });

                    chooseMyIpWindow.show();

                } catch (Exception ex) {
                    Utils.showOkPopup("Error creating node:\n\n" + ex.getMessage());
                }
            }
        });

        window.add(createNodeButton);

        JButton createNodeAndBlockchainButton = new JButton("Create new Node with new Blockchain");

        createNodeAndBlockchainButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (ownerNameTextField.getText().equals("") || savePathTextField.getText().equals("")) {
                    return;
                }

                if (getSelectedType() == NodeType.LIGHTWEIGHT) {
                    Utils.showOkPopup("Can't create light node with new blockchain!");
                    return;
                }

                try {

                    ChooseMyIpWindow chooseMyIpWindow = new ChooseMyIpWindow(new ChooseIpCallback() {

                        public void callback(String ip) {

                            try {
                                Node node = NodeManager.startNodeWithFirstBlock(getSelectedType(), ownerNameTextField.getText(), ip, savePathTextField.getText());

                                HomeWindow homeWindow = new HomeWindow(node);
                                homeWindow.show();

                                window.close();

                            } catch (Exception ex) {
                                Utils.showOkPopup("Error creating node:\n\n" + ex.getMessage());
                            }
                        }
                    });

                    chooseMyIpWindow.show();

                } catch (Exception ex) {
                    Utils.showOkPopup("Error creating node:\n\n" + ex.getMessage());
                }
            }
        });

        window.add(createNodeAndBlockchainButton);

        window.show();
    }

    private NodeType getSelectedType() {

        if (normalRadioButton.isSelected()) {
            return NodeType.NORMAL;
        } else if (lightRadioButton.isSelected()) {
            return NodeType.LIGHTWEIGHT;
        } else if (superRadioButton.isSelected()) {
            return NodeType.SUPERNODE;
        }

        return NodeType.NORMAL;
    }
}
