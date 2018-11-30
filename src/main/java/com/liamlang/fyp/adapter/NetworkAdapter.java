package com.liamlang.fyp.adapter;

import com.liamlang.fyp.Model.SignedMessage;
import com.liamlang.fyp.Utils.Utils;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;

public class NetworkAdapter {

    static final int PORT = 12345;
    static final int MAX_PACKET_SIZE = 1000000;

    public static String getMyIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static void sendSyncPacket(int height, int numConnections, String unconfirmedTransactionSetHash, InetAddress ip, KeyPair keyPair) throws Exception {
        SignedMessage m = new SignedMessage("SYNC " + getMyIp() + " " + Integer.toString(height) + " " + Integer.toString(numConnections) + " " + unconfirmedTransactionSetHash);
        m.sign(keyPair);
        sendPacket(m, ip);
    }

    public static void sendBlockPacket(int height, String block, InetAddress ip, KeyPair keyPair) throws Exception {
        SignedMessage m = new SignedMessage("BLOCK " + Integer.toString(height) + " " + block);
        m.sign(keyPair);
        sendPacket(m, ip);
    }

    public static void sendConnectionsPacket(String connections, InetAddress ip, KeyPair keyPair) throws Exception {
        SignedMessage m = new SignedMessage("CONNECTIONS " + connections);
        m.sign(keyPair);
        sendPacket(m, ip);
    }
    
    public static void sendTransactionsPacket(String transactionSet, InetAddress ip, KeyPair keyPair) throws Exception {
        SignedMessage m = new SignedMessage("UNCONFIRMED_TRANSACTION_SET " + transactionSet);
        m.sign(keyPair);
        sendPacket(m, ip);
    }

    public static void sendPacket(SignedMessage message, InetAddress ip) throws Exception {
        byte[] packet = Utils.serialize(message);
        System.out.println("Sending " + message.getMessage());
        DatagramSocket datagramSocket = new DatagramSocket();
        DatagramPacket datagramPacket = new DatagramPacket(packet, packet.length, ip, PORT);
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }

    public static SignedMessage receivePacket() throws Exception {
        DatagramSocket datagramSocket = new DatagramSocket(PORT);
        byte[] buf = new byte[MAX_PACKET_SIZE];
        DatagramPacket datagramPacket = new DatagramPacket(buf, MAX_PACKET_SIZE);
        datagramSocket.receive(datagramPacket);
        datagramSocket.close();
        byte[] bytes = datagramPacket.getData();
        return (SignedMessage) Utils.deserialize(bytes);
    }

    public interface PacketReceivedListener {

        void onPacketReceived(SignedMessage message);
    }

    public static void runWhenPacketReceived(PacketReceivedListener listener) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    SignedMessage message = receivePacket();
                    System.out.println("Received " + message.getMessage());
                    listener.onPacketReceived(message);
                } catch (Exception ex) {
                    System.out.println("Exception in NetworkAdapter.runWhenPacketReceived");
                }
                runWhenPacketReceived(listener);
            }
        };
        thread.start();
    }
}
