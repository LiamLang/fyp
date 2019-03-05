package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Component;
import com.liamlang.fyp.Model.ConnectedNode;
import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.adapter.NetworkAdapter;
import com.liamlang.fyp.service.Node.NodeType;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class PacketSender implements Serializable {

    private Node node;

    public PacketSender(Node node) {
        this.node = node;
    }

    public void sendSync(ConnectedNode connection, int bcHeight, int numConnections, ArrayList<Transaction> unconfirmedTransactionSet, PublicKey myEcPubKey) {
        try {

            if (node.getNodeType() == NodeType.LIGHTWEIGHT) {
                bcHeight = Integer.MAX_VALUE;
            }

            NetworkAdapter.sendSyncPacket(node.getMyIp(), bcHeight, numConnections, Utils.toHexString(HashUtils.sha256(Utils.serialize(unconfirmedTransactionSet))),
                    Utils.toString(Utils.serialize(node.getEcKeyPair().getPublic())), connection, node.getDsaKeyPair(), node.getOwnerName(),
                    (node.getNodeType() == NodeType.SUPERNODE ? "super" : "n"));
        } catch (Exception ex) {
            System.out.println("Exception in PacketSender.sendSync");
        }
    }

    public void sendBlocks(ConnectedNode connection, int theirHeight, int myHeight) {
        for (int i = theirHeight + 1; i <= myHeight; i++) {
            try {
                String block = Utils.toString(Utils.serialize(node.getBlockchain().getAtHeight(i)));
                NetworkAdapter.sendBlockPacket(i, block, connection, node.getDsaKeyPair(), node.getOwnerName());
            } catch (Exception ex) {
                System.out.println("Exception in PacketSender.sendBlocks");
            }
        }
    }

    public void sendConnections(ConnectedNode connection) {
        try {
            String str = Utils.toString(Utils.serialize(node.getConnections()));
            NetworkAdapter.sendConnectionsPacket(str, connection, node.getDsaKeyPair(), node.getOwnerName());
        } catch (Exception ex) {
            System.out.println("Exception in PacketSender.sendConnections");
        }
    }

    public void sendTransactions(ConnectedNode connection) {
        try {

            ArrayList<Transaction> invalidTransactions = new ArrayList<>();

            for (Transaction t : node.getUnconfirmedTransactionSet()) {
                if (!node.verifyTransaction(t, false)) {
                    invalidTransactions.add(t);
                }
            }

            for (Transaction t : invalidTransactions) {
                node.getUnconfirmedTransactionSet().remove(t);
            }

            String str = Utils.toString(Utils.serialize(node.getUnconfirmedTransactionSet()));
            NetworkAdapter.sendTransactionsPacket(str, connection, node.getDsaKeyPair(), node.getOwnerName());
        } catch (Exception ex) {
            System.out.println("Exception in PacketSender.sendTransactions");
        }
    }

    public void sendComponentHashRequest(ConnectedNode connection, String hash) {
        try {

            NetworkAdapter.sendComponentHashRequest(node.getMyIp(), hash, connection, node.getDsaKeyPair(), node.getOwnerName());
        } catch (Exception ex) {
            System.out.println("Exception in PacketSender.sendComponentHashRequest");
        }
    }

    public void sendShowComponentRequest(String ip, Component component, String confirmationStatus) {
        try {

            for (ConnectedNode connection : node.getConnections()) {
                if (connection.getIp().getHostAddress().equals(ip)) {

                    String str = Utils.toString(Utils.serialize(component));

                    NetworkAdapter.sendShowComponentRequest(str, confirmationStatus, connection, node.getDsaKeyPair(), node.getOwnerName());

                    return;
                }
            }

        } catch (Exception ex) {
            System.out.println("Exception in PacketSender.sendShowComponentRequest");
        }
    }
}
