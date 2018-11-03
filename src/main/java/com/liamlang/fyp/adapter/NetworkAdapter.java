package com.liamlang.fyp.adapter;

import com.liamlang.fyp.Utils.Utils;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkAdapter {

    static final int PORT = 12345;
    static final int MAX_PACKET_SIZE = 1024;

    public static String getMyIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static byte[] makeSyncRequest(int height) throws UnknownHostException {
        String str = "SYNC " + getMyIp() + " " + Integer.toString(height);
        System.out.println(str);
        return Utils.toByteArray(str);
    }

    public static void sendPacket(byte[] packet, InetAddress ip) throws Exception {
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
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    byte[] packet = receivePacket();
                    listener.onPacketReceived(Utils.toString(packet));
                } catch (Exception ex) {
                    System.out.println("Exception in NetworkAdapter.runWhenPacketReceived");
                }
                runWhenPacketReceived(listener);
            }
        };
        thread.start();
    }
}
