package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Model.SignedMessage;
import com.liamlang.fyp.Utils.FileUtils;
import com.liamlang.fyp.Utils.NetworkUtils;
import com.liamlang.fyp.Utils.SignatureUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.adapter.NetworkAdapter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;

public class Node implements Serializable {

    private Blockchain bc;
    private ArrayList<InetAddress> connections = new ArrayList<>();
    
    private KeyPair keyPair;

    private ArrayList<PublicKey> trustedKeys = new ArrayList<>();
    private ArrayList<PublicKey> blacklistedKeys = new ArrayList<>();
    
    public Node(Blockchain bc) {
        this.bc = bc;
        this.keyPair = SignatureUtils.generateKeyPair();
    }

    public void init() {
        NetworkAdapter.runWhenPacketReceived(new NetworkAdapter.PacketReceivedListener() {
            @Override
            public void onPacketReceived(SignedMessage message) {
                packetReceived(message);
            }
        });

        Utils.scheduleRepeatingTask(5000, new Runnable() {
            @Override
            public void run() {
                pollConnections();
            }
        });

        try {
            System.out.println("Started node with IP " + NetworkAdapter.getMyIp());
        } catch (UnknownHostException ex) {
            System.out.println("Exception in Node.init");
        }
    }

    public void addConnection(InetAddress ip) {
        for (InetAddress connection : connections) {
            if (connection.equals(ip)) {
                return;
            }
        }
        connections.add(ip);
        saveSelf();
        sendConnections(ip);
    }

    public String toString() {
        String res = "My blockchain:" + bc.toString() + "\nMy connections:";
        if (!connections.isEmpty()) {
            for (InetAddress connection : connections) {
                res = res + "\n" + connection.toString();
            }
        }
        return res;
    }

    private void saveSelf() {
        try {
            FileUtils.saveToFile(this, "node.txt");
        } catch (Exception ex) {
            System.out.println("Exception saving node state");
        }
    }

    private void pollConnections() {
        if (!connections.isEmpty()) {
            for (InetAddress ip : connections) {
                pollConnection(ip);
            }
        } else {
            System.out.println("No connections");
        }
    }

    private void pollConnection(InetAddress ip) {
        try {
            //System.out.println("Polling connecction " + ip.toString());
            NetworkAdapter.sendSyncPacket(bc.getHeight(), connections.size(), ip, keyPair);
        } catch (Exception ex) {
            System.out.println("Exception in Node.pollConnection");
        }
    }
    
    private boolean keyIsTrusted(PublicKey pub) {
        if (trustedKeys.contains(pub)) {
            return true;
        } else if (blacklistedKeys.contains(pub)) {
            return false;
        } else {
            
            boolean result = Utils.showYesNoPopup("Do you want to trust this key?\n" + pub.toString());
            if (result) {
                trustedKeys.add(pub);
            } else {
                blacklistedKeys.add(pub);
            }
            saveSelf();
            return result;
        }
    }

    private void packetReceived(SignedMessage message) {
        if (message == null) {
            return;
        }
        
        if (!message.verify()) {
            System.out.println("Failed to verify the signature of the received message!");
            return;
        }
        
        if (!keyIsTrusted(message.getPublicKey())) {
            System.out.println("We do not trust the public key that has signed this message!");
            return;
        }
        
        System.out.println("Verified that the message is signed by a trusted key");
        
        String messageStr = message.getMessage();
        if (messageStr.equals("")) {
            return;
        }
        String[] parts = messageStr.split(" ");

        if (parts[0].equals("SYNC") && parts.length == 4) {
            onSyncPacketReceived(parts[1], parts[2], parts[3]);
        }

        if (parts[0].equals("BLOCK") && parts.length >= 3) {

            // Recombine parts of the serialized block which may be split up because there happen to be spaces present
            String blockStr = "";
            for (int i = 2; i < parts.length; i++) {
                blockStr += parts[i] + " ";
            }
            blockStr = blockStr.substring(0, blockStr.length() - 1);

            onBlockPacketReceived(parts[1], blockStr);
        }
        
        if (parts[0].equals("CONNECTIONS") && parts.length >= 2) {

            // Recombine parts of the serialized connections object which may be split up because there happen to be spaces present
            String connectionsStr = "";
            for (int i = 1; i < parts.length; i++) {
                connectionsStr += parts[i] + " ";
            }
            connectionsStr = connectionsStr.substring(0, connectionsStr.length() - 1);

            onConnectionsPacketReceived(connectionsStr);
        } 
    }

    private void onSyncPacketReceived(String ip, String heightStr, String numConnections) {
        try {
            addConnection(NetworkUtils.toIp(ip));
            int height = Integer.parseInt(heightStr);
            if (height < bc.getHeight()) {
                sendBlocks(InetAddress.getByName(ip), height, bc.getHeight());
            }
            if (Integer.parseInt(numConnections) < connections.size()) {
                sendConnections(InetAddress.getByName(ip));
            }
        } catch (Exception ex) {
            System.out.println("Exception in Node.onSyncPacketReceived");
        }
    }

    private void onBlockPacketReceived(String heightStr, String blockStr) {
        try {
            int height = Integer.parseInt(heightStr);

            // TODO need to check the block actually fits our chain (w.r.t. data)
            if (height == bc.getHeight() + 1) {
                Block block = (Block) Utils.deserialize(Utils.toByteArray(blockStr));
                bc.addToTop(block);
                saveSelf();
            }

        } catch (Exception ex) {
            System.out.println("Exception in Node.onBlockPacketReceived");
        }
    }
    
    private void onConnectionsPacketReceived(String otherConnectionsStr) {
        try {
            ArrayList<InetAddress> otherConnections = (ArrayList<InetAddress>) Utils.deserialize(Utils.toByteArray(otherConnectionsStr));
            for (InetAddress ip : otherConnections) {
                if (!connections.contains(ip) && !ip.getHostAddress().equals(NetworkAdapter.getMyIp())) {
                    connections.add(ip);
                }
            }            
        } catch (Exception ex) {
            System.out.println("Exception in Node.onConnectionsPacketRecevied");
        }
    }

    private void sendBlocks(InetAddress ip, int theirHeight, int myHeight) {
        for (int i = theirHeight + 1; i <= myHeight; i++) {
            try {
                String block = Utils.toString(Utils.serialize(bc.getAtHeight(i)));
                NetworkAdapter.sendBlockPacket(i, block, ip, keyPair);
            } catch (Exception ex) {
                System.out.println("Exception in Node.sendBlocks");
            }
        }
    }

    private void sendConnections(InetAddress ip) {
        try {
            String str = Utils.toString(Utils.serialize(connections));
            NetworkAdapter.sendConnectionsPacket(str, ip, keyPair);
        } catch (Exception ex) {
            System.out.println("Exception in Node.sendConnections");
        }
    }
}
