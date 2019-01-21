package com.liamlang.fyp.service;

import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.adapter.NetworkAdapter;
import java.io.Serializable;
import java.net.InetAddress;

public class PacketSender implements Serializable {
    
    private Node node;
    
    public PacketSender(Node node) {
        this.node = node;
    }
    
    public void sendBlocks(InetAddress ip, int theirHeight, int myHeight) {
        for (int i = theirHeight + 1; i <= myHeight; i++) {
            try {
                String block = Utils.toString(Utils.serialize(node.getBlockchain().getAtHeight(i)));
                NetworkAdapter.sendBlockPacket(i, block, ip, node.getKeyPair());
            } catch (Exception ex) {
                System.out.println("Exception in Node.sendBlocks");
            }
        }
    }

    public void sendConnections(InetAddress ip) {
        try {
            String str = Utils.toString(Utils.serialize(node.getConnections()));
            NetworkAdapter.sendConnectionsPacket(str, ip, node.getKeyPair());
        } catch (Exception ex) {
            System.out.println("Exception in Node.sendConnections");
        }
    }

    public void sendTransactions(InetAddress ip) {
        try {
            String str = Utils.toString(Utils.serialize(node.getUnconfirmedTransactionSet()));
            NetworkAdapter.sendTransactionsPacket(str, ip, node.getKeyPair());
        } catch (Exception ex) {
            System.out.println("Exception in Node.sendTransactions");
        }
    }
}
