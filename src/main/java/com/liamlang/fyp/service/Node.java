package com.liamlang.fyp.service;

import com.liamlang.fyp.Model.Block;
import com.liamlang.fyp.Model.Blockchain;
import com.liamlang.fyp.Utils.NetworkUtils;
import com.liamlang.fyp.Utils.Utils;
import com.liamlang.fyp.adapter.NetworkAdapter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Node {

    private Blockchain bc;
    private ArrayList<InetAddress> connections = new ArrayList<>();

    public Node(Blockchain bc) {
        this.bc = bc;

        NetworkAdapter.runWhenPacketReceived(new NetworkAdapter.PacketReceivedListener() {
            @Override
            public void onPacketReceived(String packet) {
                packetReceived(packet);
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
            System.out.println("Exception in Node constructor");
        }
    }

    public void addConnection(InetAddress ip) {
        for (InetAddress connection : connections) {
            if (connection.equals(ip)) {
                return;
            }
        }
        connections.add(ip);
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
            System.out.println("Polling connecction " + ip.toString());
            NetworkAdapter.sendSyncPacket(bc.getHeight(), ip);
        } catch (Exception ex) {
            System.out.println("Exception in Node.pollConnection");
        }
    }

    private void packetReceived(String packet) {
        if (packet.equals("")) {
            return;
        }
        String[] parts = packet.split(" ");

        if (parts[0].equals("SYNC") && parts.length == 3) {
            onSyncPacketReceived(parts[1], parts[2].trim());
        }

        if (parts[0].equals("BLOCK") && parts.length == 3) {
            onBlockPacketReceived(parts[1], parts[2].trim());
        }
    }

    private void onSyncPacketReceived(String ip, String heightStr) {
        try {
            addConnection(NetworkUtils.toIp(ip));
            int height = Integer.parseInt(heightStr);
            if (height < bc.getHeight()) {
                sendBlocks(InetAddress.getByName(ip), height, bc.getHeight());
            }
        } catch (Exception ex) {
            System.out.println("Exception in Node.onSyncPacketReceived");
        }
    }
    
    private void onBlockPacketReceived(String heightStr, String blockStr) {
        try {
            int height = Integer.parseInt(heightStr);
            
            // TODO this is very naive
            
            if (height == bc.getHeight() + 1) {
                Block block = (Block) Utils.deserialize(Utils.toByteArray(blockStr));
                bc.addToTop(block);
            }
            
        } catch (Exception ex) {
            System.out.println("Exception in Node.onBlockPacketReceived");
        }
    }

    private void sendBlocks(InetAddress ip, int theirHeight, int myHeight) {
        for (int i = theirHeight + 1; i <= myHeight; i++) {
            try {
                String block = Utils.toString(Utils.serialize(bc.getAtHeight(i)));
                NetworkAdapter.sendBlockPacket(i, block, ip);
            } catch (Exception ex) {
                System.out.println("Exception in Node.sendBlocks");
            }
        }
    }
}
