package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.BlockData;
import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.SignedMessage;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.FileUtils;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.adapter.NetworkAdapter;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;

public class Node implements Serializable {

    private ReceivedPacketHandler receivedPacketHandler;
    private PacketSender packetSender;
    private TransactionBuilder transactionBuilder;
    private TransactionVerifier transactionVerifier;
    
    private Blockchain bc;
    
    private ArrayList<InetAddress> connections = new ArrayList<>();
    
    private ArrayList<Transaction> unconfirmedTransactionSet = new ArrayList<>();
    
    private ArrayList<Component> unspentComponents = new ArrayList<>();

    private String ownerName;
    
    private KeyPair keyPair;

    private ArrayList<PublicKey> trustedKeys = new ArrayList<>();
    private ArrayList<PublicKey> blacklistedKeys = new ArrayList<>();

    public Node(Blockchain bc, String ownerName, KeyPair keyPair) {
        this.bc = bc;
        this.ownerName = ownerName;
        this.keyPair = keyPair;
    }

    public void init() {
        
        receivedPacketHandler = new ReceivedPacketHandler(this);
        packetSender = new PacketSender(this);
        transactionBuilder = new TransactionBuilder(this);
        transactionVerifier = new TransactionVerifier(this);
        
        NetworkAdapter.runWhenPacketReceived(new NetworkAdapter.PacketReceivedListener() {
            @Override
            public void onPacketReceived(SignedMessage message) {
                receivedPacketHandler.onPacketReceived(message);
            }
        });

        Utils.scheduleRepeatingTask(2000, new Runnable() {
            @Override
            public void run() {
                syncWithConnections();
            }
        });

        try {
            System.out.println("Started node with IP " + NetworkAdapter.getMyIp());
        } catch (UnknownHostException ex) {
            System.out.println("Exception in Node.init");
        }
        
        
        System.out.println("Hash of my public key: " + Utils.toHexString(HashUtils.sha256(keyPair.getPublic().getEncoded())));
    }

    public void addConnection(InetAddress ip) {
        for (InetAddress connection : connections) {
            if (connection.equals(ip)) {
                return;
            }
        }
        connections.add(ip);
        saveSelf();
        packetSender.sendConnections(ip);
    }

    public void broadcastTransaction(Transaction t) {
        if (t != null && isValidTransaction(t)) {
            unconfirmedTransactionSet.add(t);
        }
        saveSelf();
    }

    public void startCreatingBlocks() {
        Utils.scheduleRepeatingTask(10000, new Runnable() {
            @Override
            public void run() {
                createBlock();
            }
        });
    }

    public String toString() {
        String res = "My blockchain:" + bc.toString() + "\nMy connections:";
        if (!connections.isEmpty()) {
            for (InetAddress connection : connections) {
                res = res + "\n" + connection.toString();
            }
        }
        res += "\nI have " + Integer.toString(unconfirmedTransactionSet.size()) + " unconfirmed transactions";
        return res;
    }

    public void saveSelf() {
        try {
            FileUtils.saveToFile(this, "node.txt");
        } catch (Exception ex) {
            System.out.println("Exception saving node state");
        }
    }

    private void syncWithConnections() {
        if (!connections.isEmpty()) {
            for (InetAddress ip : connections) {
                syncWithConnection(ip);
            }
        } else {
            System.out.println("No connections");
        }
    }

    private void createBlock() {
        try {
            if (bc.getHeight() == 0) {
                return;
            }

            BlockData blockData = new BlockData(unconfirmedTransactionSet);
            Block block = new Block(bc.getTop(), blockData);
            unconfirmedTransactionSet = new ArrayList<>();
            
            bc.addToTop(block);
            saveSelf();
            
        } catch (IOException ex) {
            System.out.println("Exception in Node.createBlock");
        }
    }

    private void syncWithConnection(InetAddress ip) {
        try {
            NetworkAdapter.sendSyncPacket(bc.getHeight(), connections.size(), Utils.toHexString(HashUtils.sha256(Utils.serialize(unconfirmedTransactionSet))), ip, keyPair);
        } catch (Exception ex) {
            System.out.println("Exception in Node.pollConnection");
        }
    }

    public boolean keyIsTrusted(PublicKey pub) {
        if (trustedKeys.contains(pub)) {
            return true;
        } else if (blacklistedKeys.contains(pub)) {
            return false;
        } else {

            boolean result = Utils.showYesNoPopup("Do you want to trust this key?\n" + Utils.toHexString(HashUtils.sha256(pub.getEncoded())));
            if (result) {
                trustedKeys.add(pub);
            } else {
                blacklistedKeys.add(pub);
            }
            saveSelf();
            return result;
        }
    }

    public boolean isValidTransaction(Transaction t) {
        // Check whether an incoming unconfirmed transaction is valid, according to my blockchain

        // TODO
        return true;
    }
    
    public PacketSender getPacketSender() {
        return packetSender;
    }
    
    public Blockchain getBlockchain() {
        return bc;
    }
        
    public ArrayList<InetAddress> getConnections() {
        return connections;
    }
    
    public ArrayList<Transaction> getUnconfirmedTransactionSet() {
        return unconfirmedTransactionSet;
    }
    
    public KeyPair getKeyPair() {
        return keyPair;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwneName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public ArrayList<Component> getUnspentComponents() {
        return unspentComponents;
    }
}
