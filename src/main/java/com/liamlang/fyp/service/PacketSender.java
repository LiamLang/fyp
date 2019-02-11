package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Transaction;
import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.adapter.NetworkAdapter;
import java.io.Serializable;
import java.net.InetAddress;
import java.security.PublicKey;
import java.util.ArrayList;

public class PacketSender implements Serializable {

    private Node node;

    public PacketSender(Node node) {
        this.node = node;
    }

    public void sendSync(InetAddress ip, int bcHeight, int numConnections, ArrayList<Transaction> unconfirmedTransactionSet, PublicKey myEcPubKey) {
        try {
            NetworkAdapter.sendSyncPacket(bcHeight, numConnections, Utils.toHexString(HashUtils.sha256(Utils.serialize(unconfirmedTransactionSet))),
                    Utils.toString(Utils.serialize(node.getEcKeyPair().getPublic())), ip, node.getDsaKeyPair(), node.getOwnerName());
        } catch (Exception ex) {
            System.out.println("Exception in PacketSender.sendSync");
        }
    }

    public void sendBlocks(InetAddress ip, int theirHeight, int myHeight) {
        for (int i = theirHeight + 1; i <= myHeight; i++) {
            try {
                String block = Utils.toString(Utils.serialize(node.getBlockchain().getAtHeight(i)));
                NetworkAdapter.sendBlockPacket(i, block, ip, node.getDsaKeyPair(), node.getOwnerName());
            } catch (Exception ex) {
                System.out.println("Exception in PacketSender.sendBlocks");
            }
        }
    }

    public void sendConnections(InetAddress ip) {
        try {
            String str = Utils.toString(Utils.serialize(node.getConnections()));
            NetworkAdapter.sendConnectionsPacket(str, ip, node.getDsaKeyPair(), node.getOwnerName());
        } catch (Exception ex) {
            System.out.println("Exception in PacketSender.sendConnections");
        }
    }

    public void sendTransactions(InetAddress ip) {
        try {
            String str = Utils.toString(Utils.serialize(node.getUnconfirmedTransactionSet()));
            NetworkAdapter.sendTransactionsPacket(str, ip, node.getDsaKeyPair(), node.getOwnerName());
        } catch (Exception ex) {
            System.out.println("Exception in PacketSender.sendTransactions");
        }
    }
}
