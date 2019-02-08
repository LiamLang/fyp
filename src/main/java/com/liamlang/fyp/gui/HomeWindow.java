package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
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

                if (!lastBlockHash.equals(node.getBlockchain().getTop().getHash()) || numConnections != node.getConnections().size()) {

                    lastBlockHash = node.getBlockchain().getTop().getHash();
                    numConnections = node.getConnections().size();

                    updateWindow();
                    window.refresh();
                }
            }
        });
    }

    public void updateWindow() {

        window.removeAll();

        window.addImage("src/main/resources/home.png");

        window.addVerticalSpace(20);

        window.addLabel("Connections: " + numConnections);

        JButton viewConnectionsButton = new JButton("View Connections");

        viewConnectionsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ConnectionsWindow win = new ConnectionsWindow(node);
                win.show();
            }
        });

        window.add(viewConnectionsButton);

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

        if (node.getBlockchain().getHeight() > 0) {
            window.addLabel("Last Block Hash: " + node.getBlockchain().getTop().getHash());
        } else {
            window.addLabel("No Blocks in Blockchain.");
        }

        JButton viewBlockchainButton = new JButton("View Blockchain");

        viewBlockchainButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ViewBlockchainWindow win = new ViewBlockchainWindow(node.getBlockchain(), node);
                win.show();
            }
        });

        window.add(viewBlockchainButton);

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

        JTextField componentHashTextField = new JTextField();
        window.add(componentHashTextField);

        JButton findComponentHashButton = new JButton("Find");

        findComponentHashButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                boolean resultFound = false;

                String hash = componentHashTextField.getText();

                if (hash.equals("")) {
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

        JTextField componentInfoTextField = new JTextField();
        window.add(componentInfoTextField);

        JButton findComponentInfoButton = new JButton("Find");

        findComponentInfoButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

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

        JTextField blockHashTextField = new JTextField();
        window.add(blockHashTextField);

        JButton findBlockButton = new JButton("Find");

        findBlockButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

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
