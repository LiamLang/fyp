package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import com.liamlang.fyp.service.Node.NodeType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;

public class HomeWindow {

    private WindowBase window;

    private final Node node;

    private String lastBlockHash;
    private int numConnections;

    public HomeWindow(Node node) {
        this.node = node;
        if (node.getBlockchain().getHeight() > 0) {
            lastBlockHash = node.getBlockchain().getTop().getHash();
        } else {
            lastBlockHash = "";
        }
        this.numConnections = node.getConnections().size();

    }

    public void show() {

        window = new WindowBase("Home", 600);
        window.init();

        updateWindow();

        window.show();

        Utils.scheduleRepeatingTask(1000, new Runnable() {
            @Override
            public void run() {

                if (node.getBlockchain().getHeight() > 0) {
                    if (!lastBlockHash.equals(node.getBlockchain().getTop().getHash())) {

                        lastBlockHash = node.getBlockchain().getTop().getHash();

                        updateWindow();
                        window.refresh();
                    }
                }

                if (numConnections != node.getConnections().size()) {

                    numConnections = node.getConnections().size();

                    updateWindow();
                    window.refresh();
                }
            }
        });
    }

    public void updateWindow() {

        window.removeAll();

        window.addImage("resources/home.png");

        window.addVerticalSpace(20);

        window.addLabel("Node type: " + (node.getNodeType() == NodeType.NORMAL ? "Normal (full node)" : node.getNodeType() == NodeType.LIGHTWEIGHT ? "Lightweight" : "Supernode"));

        window.addVerticalSpace(10);

        window.addLabel("Connections: " + numConnections);

        window.addVerticalSpace(5);

        JButton viewConnectionsButton = new JButton("View Connections");

        viewConnectionsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ConnectionsWindow win = new ConnectionsWindow(node);
                win.show();
            }
        });

        window.add(viewConnectionsButton);

        window.addVerticalSpace(5);

        JButton viewDetailsButton = new JButton("View My Details");

        viewDetailsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                MyDetailsWindow win = new MyDetailsWindow(node);
                win.show();
            }
        });

        window.add(viewDetailsButton);

        window.addVerticalSpace(20);

        window.addLabel("Blocks in Chain: " + Integer.toString(node.getBlockchain().getHeight()));

        window.addVerticalSpace(5);

        if (node.getBlockchain().getHeight() > 0) {
            window.addLabel("Last Block Hash: " + node.getBlockchain().getTop().getHash());
        } else {
            window.addLabel("No Blocks in Blockchain.");
        }

        window.addVerticalSpace(5);

        if (node.getNodeType() == NodeType.LIGHTWEIGHT) {

            window.addLabel("Light node does not store a copy of the blockchain.");

        } else {

            JButton viewBlockchainButton = new JButton("View Blockchain");

            viewBlockchainButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ViewBlockchainWindow win = new ViewBlockchainWindow(node.getBlockchain(), node);
                    win.show();
                }
            });

            window.add(viewBlockchainButton);

        }
        window.addVerticalSpace(20);

        JButton createComponentButton = new JButton("Create Component...");

        createComponentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                NewComponentTransactionWindow win = new NewComponentTransactionWindow(node);
                win.show();
            }
        });

        window.add(createComponentButton);

        window.addVerticalSpace(20);

        window.addLabel("Find Component by Hash:");

        window.addVerticalSpace(5);

        JTextField componentHashTextField = new JTextField();
        window.add(componentHashTextField);

        window.addVerticalSpace(5);

        JButton findComponentHashButton = new JButton("Find");

        findComponentHashButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                boolean resultFound = false;

                String hash = componentHashTextField.getText();

                if (hash.equals("")) {
                    return;
                }

                if (node.getNodeType() == NodeType.LIGHTWEIGHT) {

                    if (node.getConnections().size() < 1) {
                        Utils.showOkPopup("Not connected to any supernodes.");
                        return;
                    }

                    node.getPacketSender().sendComponentHashRequest(node.getConnections().get(0), hash);

                    return;
                }

                for (Component component : node.getUnspentComponents()) {

                    if (component.getHash().equals(hash)) {

                        ViewComponentWindow win = new ViewComponentWindow(component, node);
                        win.show();

                        resultFound = true;
                    }
                }

                if (!resultFound) {
                    Utils.showOkPopup("No results found!");
                }
            }
        });

        window.add(findComponentHashButton);

        window.addVerticalSpace(20);

        window.addLabel("Find Component by Information:");

        window.addVerticalSpace(5);

        JTextField componentInfoTextField = new JTextField();
        window.add(componentInfoTextField);

        window.addVerticalSpace(5);

        JButton findComponentInfoButton = new JButton("Find");

        findComponentInfoButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO adapt for light node
                boolean resultFound = false;

                String info = componentInfoTextField.getText();

                if (info.equals("")) {
                    return;
                }

                for (Component component : node.getUnspentComponents()) {

                    // Inefficient, but it'll do for this proof of concept
                    if (component.getInfo().toString().contains(info)) {

                        ViewComponentWindow win = new ViewComponentWindow(component, node);
                        win.show();

                        resultFound = true;
                    }
                }

                if (!resultFound) {
                    Utils.showOkPopup("No results found!");
                }
            }
        });

        window.add(findComponentInfoButton);

        window.addVerticalSpace(20);

        window.addLabel("Find Block by Hash:");

        window.addVerticalSpace(5);

        JTextField blockHashTextField = new JTextField();
        window.add(blockHashTextField);

        window.addVerticalSpace(5);

        JButton findBlockButton = new JButton("Find");

        findBlockButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO adapt for light node
                String hash = blockHashTextField.getText();

                if (hash.equals("")) {
                    return;
                }

                Block block = node.getBlockchain().getBlockWithHash(hash);

                if (block == null) {
                    Utils.showOkPopup("No results found!");
                    return;
                }

                ViewBlockWindow win = new ViewBlockWindow(block, node);
                win.show();
            }
        });

        window.add(findBlockButton);

        window.addVerticalSpace(20);

    }
}
