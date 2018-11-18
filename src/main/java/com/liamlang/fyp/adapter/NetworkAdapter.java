package com.liamlang.fyp.adapter;

import com.liamlang.fyp.Utils.Utils;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkAdapter {

    static final int PORT = 12345;
    static final int MAX_PACKET_SIZE = 1000000;

    public static String getMyIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static void sendSyncPacket(int height, int numConnections, InetAddress ip) throws Exception {
        sendPacket("SYNC " + getMyIp() + " " + Integer.toString(height) + " " + Integer.toString(numConnections), ip);
    }

    public static void sendBlockPacket(int height, String block, InetAddress ip) throws Exception {
        sendPacket("BLOCK " + Integer.toString(height) + " " + block, ip);
    }

    public static void sendConnectionsPacket(String connections, InetAddress ip) throws Exception {
        sendPacket("CONNECTIONS " + connections, ip);
    }

    public static void sendPacket(String packetStr, InetAddress ip) throws Exception {
        System.out.println("Sending " + packetStr);
        byte[] packet = Utils.toByteArray(packetStr);
        DatagramSocket datagramSocket = new DatagramSocket();
        DatagramPacket datagramPacket = new DatagramPacket(packet, packet.length, ip, PORT);
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }

    public static byte[] receivePacket() throws Exception {
        DatagramSocket datagramSocket = new DatagramSocket(PORT);
        byte[] buf = new byte[MAX_PACKET_SIZE];
        DatagramPacket datagramPacket = new DatagramPacket(buf, MAX_PACKET_SIZE);
        datagramSocket.receive(datagramPacket);
        datagramSocket.close();
        return datagramPacket.getData();
    }

    public interface PacketReceivedListener {

        void onPacketReceived(String packet);
    }

    public static void runWhenPacketReceived(PacketReceivedListener listener) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    byte[] packet = receivePacket();
                    String packetStr = Utils.toString(packet).trim();
                    System.out.println("Received " + packetStr);
                    listener.onPacketReceived(packetStr);
                } catch (Exception ex) {
                    System.out.println("Exception in NetworkAdapter.runWhenPacketReceived");
                }
                runWhenPacketReceived(listener);
            }
        };
        thread.start();
    }
}
