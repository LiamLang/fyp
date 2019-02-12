package com.liamlang.fyp.gui;

import com.liamlang.fyp.Model.ConnectedNode;
import com.liamlang.fyp.Model.TrustedSignee;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.NetworkUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.service.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.security.PublicKey;
import javax.swing.JButton;
import javax.swing.JTextField;

public class ConnectionsWindow {

    private WindowBase window;

    private final Node node;

    private byte[] connectionsHash;
    private int numTrustedKeys;
    private int numBlacklistedKeys;

    public ConnectionsWindow(Node node) {
        this.node = node;
        this.connectionsHash = HashUtils.sha256(Utils.serialize(node.getConnections()));
        this.numTrustedKeys = node.getTrustedSignees().size();
        this.numBlacklistedKeys = node.getBlacklistedKeys().size();
    }

    public void show() {

        window = new WindowBase("My Connections", 600);
        window.init();

        updateWindow();

        window.show();

        Utils.scheduleRepeatingTask(1000, new Runnable() {
            @Override
            public void run() {

                if (connectionsHash != HashUtils.sha256(Utils.serialize(node.getConnections()))
                        || numTrustedKeys != node.getTrustedSignees().size()
                        || numBlacklistedKeys != node.getBlacklistedKeys().size()) {

                    connectionsHash = HashUtils.sha256(Utils.serialize(node.getConnections()));
                    numTrustedKeys = node.getTrustedSignees().size();
                    numBlacklistedKeys = node.getBlacklistedKeys().size();

                    updateWindow();
                    window.refresh();
                }
            }
        });
    }

    public void updateWindow() {

        window.removeAll();

        window.addImage("src/main/resources/connections.png");

        window.addVerticalSpace(20);

        window.addSelectableTextField("My IP: " + node.getMyIp());

        window.addVerticalSpace(20);

        window.addLabel("Connected Nodes: " + Integer.toString(node.getConnections().size()));

        window.addVerticalSpace(10);

        for (ConnectedNode connection : node.getConnections()) {

            window.addSelectableTextField(connection.getIp().toString());
            window.addSelectableTextField("Encryption Public Key: " + Utils.toHexString(connection.getEcPubKey().getEncoded()));

            window.addVerticalSpace(5);

        }

        window.addVerticalSpace(10);

        window.addLabel("Connect to new Node:");

        window.addVerticalSpace(5);

        JTextField ipAddressField = new JTextField();
        window.add(ipAddressField);

        window.addVerticalSpace(5);

        JButton addButton = new JButton("Connect");
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String text = ipAddressField.getText();
                if (text.equals("")) {
                    return;
                }

                try {
                    InetAddress ip = NetworkUtils.toIp(text);
                    node.addConnection(new ConnectedNode(ip));

                    Utils.showOkPopup("Added connection to " + ip.toString() + "!");
                } catch (Exception ex) {

                    Utils.showOkPopup("Error!");
                }
            }
        });

        window.add(addButton);

        window.addVerticalSpace(20);

        window.addImage("src/main/resources/key.png");

        window.addVerticalSpace(20);

        window.addLabel("Trusted Signing Keys: " + Integer.toString(node.getTrustedSignees().size()));

        window.addVerticalSpace(10);

        for (TrustedSignee signee : node.getTrustedSignees()) {

            window.addSelectableTextField(signee.getName());

            window.addSelectableTextField(Utils.toHexString(HashUtils.sha256(signee.getPubkey().getEncoded())));

            window.addVerticalSpace(10);
        }

        window.addLabel("Blacklisted Signing Keys: " + Integer.toString(node.getBlacklistedKeys().size()));

        window.addVerticalSpace(10);

        for (PublicKey key : node.getBlacklistedKeys()) {

            window.addSelectableTextField(Utils.toHexString(HashUtils.sha256(key.getEncoded())));
        }

    }
}
