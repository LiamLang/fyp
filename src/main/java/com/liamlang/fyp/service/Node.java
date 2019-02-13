package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.BlockData;
import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.ConnectedNode;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Model.TrustedSignee;
import com.liamlang.fyp.Utils.EncryptionUtils;
import com.liamlang.fyp.Utils.FileUtils;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.SignatureUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.adapter.NetworkAdapter;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;

public class Node implements Serializable {

    private ReceivedPacketHandler receivedPacketHandler;
    private PacketSender packetSender;
    private TransactionBuilder transactionBuilder;
    private TransactionVerifier transactionVerifier;

    private Blockchain bc;

    private ArrayList<ConnectedNode> connections = new ArrayList<>();

    private ArrayList<Transaction> unconfirmedTransactionSet = new ArrayList<>();

    private ArrayList<Component> unspentComponents = new ArrayList<>();

    private String ownerName;

    private KeyPair dsaKeyPair;
    private KeyPair ecKeyPair;

    private ArrayList<TrustedSignee> trustedSignees = new ArrayList<>();
    private ArrayList<PublicKey> blacklistedKeys = new ArrayList<>();

    private boolean isCreatingBlocks;

    private String saveFileName;

    public Node(Blockchain bc, String ownerName, String saveFileName) {
        this.bc = bc;
        this.ownerName = ownerName;
        this.dsaKeyPair = SignatureUtils.generateDsaKeyPair();
        this.ecKeyPair = EncryptionUtils.generateEcKeyPair();
        this.isCreatingBlocks = false;
        this.saveFileName = saveFileName;
    }

    public void init() {

        receivedPacketHandler = new ReceivedPacketHandler(this);
        packetSender = new PacketSender(this);
        transactionBuilder = new TransactionBuilder(this);
        transactionVerifier = new TransactionVerifier(this);

        NetworkAdapter.runWhenPacketReceived(new NetworkAdapter.PacketReceivedListener() {

            @Override
            public void onPacketReceived(byte[] bytes) {

                receivedPacketHandler.onPacketReceived(bytes);
            }
        });

        Utils.scheduleRepeatingTask(2000, new Runnable() {
            @Override
            public void run() {
                syncWithConnections();
            }
        });

        System.out.println("Started node with IP " + getMyIp());
    }

    public String getMyIp() {
        try {
            return NetworkAdapter.getMyIp();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error getting my IP!";
        }
    }

    public void addConnection(ConnectedNode newConnection) {

        ConnectedNode connectionForDeletion = null;

        for (ConnectedNode connection : connections) {          
            
            if (connection.getIp().getHostAddress().equals(newConnection.getIp().getHostAddress())) {

                connectionForDeletion = connection;
            }
        }

        // If a connection with the same IP is present delete the old one
        // (i.e. replacing the encryption key with the new one)
        // This does not open a vulnetability, as messages not signed with a
        // trusted signing key will be ignored
        if (connectionForDeletion != null) {
            connections.remove(connectionForDeletion);
        }

        connections.add(newConnection);
        saveSelf();
    }

    public void broadcastTransaction(Transaction t) {
        if (t != null && isValidTransaction(t)) {
            unconfirmedTransactionSet.add(t);
        }
        saveSelf();
    }

    public void startCreatingBlocks() {

        isCreatingBlocks = true;

        Utils.scheduleRepeatingTask(5000, new Runnable() {
            @Override
            public void run() {

                if (unconfirmedTransactionSet.size() > 0) {
                    createBlock();
                }
            }
        });
    }

    public boolean isCreatingBlocks() {
        return isCreatingBlocks;
    }

    public boolean isUnspent(Component component) {

        for (Component unspentComponent : unspentComponents) {

            if (unspentComponent.getHash().equals(component.getHash()) && component.verifyHash()) {

                return true;
            }
        }

        return false;
    }

    public String toString() {
        return "My blockchain:" + bc.toString() + "\nMy connections: " + Integer.toString(connections.size())
                + "\nI have " + Integer.toString(unconfirmedTransactionSet.size()) + " unconfirmed transactions";
    }

    public void saveSelf() {
        
        try {
            FileUtils.saveToFile(this, saveFileName);
        } catch (Exception ex) {
            System.out.println("Exception saving node state");
        }
    }

    private void syncWithConnections() {
        if (!connections.isEmpty()) {
            for (ConnectedNode connection : connections) {
                syncWithConnection(connection);
            }
        } else {
            System.out.println("No connections");
        }
    }

    public void createBlock() {
        try {
            if (bc.getHeight() == 0) {
                return;
            }

            BlockData blockData = new BlockData(unconfirmedTransactionSet);
            Block block = new Block(bc.getTop(), blockData);
            unconfirmedTransactionSet = new ArrayList<>();

            bc.addToTop(block, this);
            saveSelf();

        } catch (IOException ex) {
            System.out.println("Exception in Node.createBlock");
        }
    }

    private void syncWithConnection(ConnectedNode connection) {

        packetSender.sendSync(connection, bc.getHeight(), connections.size(), unconfirmedTransactionSet, ecKeyPair.getPublic());
    }

    public boolean keyIsTrusted(PublicKey pub, String signee) {

        for (TrustedSignee trustedSignee : trustedSignees) {
            if (trustedSignee.getPubkey().equals(pub) && trustedSignee.getName().equals(signee)) {
                return true;
            }
        }
        if (blacklistedKeys.contains(pub)) {
            return false;
        } else {

            boolean result = Utils.showYesNoPopup("Do you want to trust this key?\n\nSignee: " + signee
                    + "\nKey hash: " + Utils.toHexString(HashUtils.sha256(pub.getEncoded())));
            if (result) {
                trustedSignees.add(new TrustedSignee(pub, signee));
            } else {
                blacklistedKeys.add(pub);
            }
            saveSelf();
            return result;
        }
    }

    public Component getUnspentComponent(String hash) {

        for (Component component : unspentComponents) {
            if (component.getHash().equals(hash)) {
                return component;
            }
        }

        return null;
    }

    public boolean isValidTransaction(Transaction t) {
        return transactionVerifier.verify(t);
    }

    public PacketSender getPacketSender() {
        return packetSender;
    }

    public TransactionBuilder getTransactionBuilder() {
        return transactionBuilder;
    }

    public Blockchain getBlockchain() {
        return bc;
    }

    public ArrayList<ConnectedNode> getConnections() {
        return connections;
    }

    public ArrayList<Transaction> getUnconfirmedTransactionSet() {
        return unconfirmedTransactionSet;
    }

    public KeyPair getDsaKeyPair() {
        return dsaKeyPair;
    }

    public KeyPair getEcKeyPair() {
        return ecKeyPair;
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

    public ArrayList<TrustedSignee> getTrustedSignees() {
        return trustedSignees;
    }

    public ArrayList<PublicKey> getBlacklistedKeys() {
        return blacklistedKeys;
    }
}
