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

                if (!Utils.toString(connectionsHash).equals(Utils.toString(HashUtils.sha256(Utils.serialize(node.getConnections()))))
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

        window.addImage("resources/connections.png");

        window.addVerticalSpace(20);

        window.addSelectableTextField("My IP and Port: " + node.getMyIp() + ":" + Integer.toString(node.getMyPort()));

        window.addVerticalSpace(20);

        window.addLabel("Connected Nodes: " + Integer.toString(node.getConnections().size()));

        window.addVerticalSpace(10);

        for (ConnectedNode connection : node.getConnections()) {

            window.addSelectableTextField(connection.getIp().toString() + ":" + Integer.toString(connection.getPort()));
            if (connection.getEcPubKey() != null) {
                window.addSelectableTextField("Encryption Public Key: " + Utils.toHexString(connection.getEcPubKey().getEncoded()));
            } else {
                window.addSelectableTextField("Encryption Public Key: Not yet known");
            }

            window.addVerticalSpace(10);

        }

        window.addVerticalSpace(10);

        window.addLabel("Connect to new Node (IP, Port):");

        window.addVerticalSpace(5);

        JTextField ipAddressField = new JTextField();
        window.add(ipAddressField);
        
        JTextField portField = new JTextField();
        window.add(portField);
        portField.setText("12345");

        window.addVerticalSpace(5);

        JButton addButton = new JButton("Connect");
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String ipText = ipAddressField.getText();
                String portText = portField.getText();
                if (ipText.equals("") || portText.equals("")) {
                    return;
                }

                try {
                    InetAddress ip = NetworkUtils.toIp(ipText);
                    int port = Integer.parseInt(portText);
                    
                    if (port < 0 || port > 65535) {
                        throw new Exception();
                    }
                    
                    node.addConnection(new ConnectedNode(ip, port));

                    Utils.showOkPopup("Added connection to " + ip.toString() + ":" + Integer.toString(port));
                } catch (Exception ex) {

                    Utils.showOkPopup("Error!");
                }
            }
        });

        window.add(addButton);

        window.addVerticalSpace(20);

        window.addImage("resources/key.png");

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
