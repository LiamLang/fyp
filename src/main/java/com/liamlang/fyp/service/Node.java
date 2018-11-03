package com.liamlang.fyp.service;

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
            byte[] packet = NetworkAdapter.makeSyncRequest(bc.getHeight());
            NetworkAdapter.sendPacket(packet, ip);
        } catch (Exception ex) {
            System.out.println("Exception in Node.pollConnection");
        }
    }

    private void packetReceived(String packet) {
        System.out.println("Packet received!\n" + packet);
        if (packet.equals("")) {
            return;
        }
        String[] parts = packet.split(" ");
        if (parts.length < 3) {
            return;
        }
        if (parts[0].equals("SYNC")) {
            onSyncPacketReceived(parts[1], parts[2]);
        }
    }

    private void onSyncPacketReceived(String ip, String heightStr) {
        try {
            addConnection(NetworkUtils.toIp(ip));
            int height = Integer.parseInt(heightStr);
            if (height < bc.getHeight()) {

                System.out.println("I need to send block(s)!");
                // TODO

            }
        } catch (Exception ex) {
            System.out.println("Exception in Node.onSyncPacketReceived");
        }
    }
}
