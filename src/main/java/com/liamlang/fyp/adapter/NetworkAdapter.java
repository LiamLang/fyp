package com.liamlang.fyp.adapter;

import com.liamlang.fyp.Model.ConnectedNode;
import com.liamlang.fyp.Model.EncryptedMessage;
import com.liamlang.fyp.Model.SignedMessage;
import com.liamlang.fyp.Utils.EncryptionUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
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

    public static void sendSyncPacket(int height, int numConnections, String unconfirmedTransactionSetHash, String myEcPubKey, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("SYNC " + getMyIp() + " " + Integer.toString(height) + " " + Integer.toString(numConnections) + " " + unconfirmedTransactionSetHash + " " + myEcPubKey);
        m.sign(myDsaKeyPair, myName);

        sendPacket(m, connection.getIp());
    }

    public static void sendBlockPacket(int height, String block, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("BLOCK " + Integer.toString(height) + " " + block);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void sendConnectionsPacket(String connections, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("CONNECTIONS " + connections);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void sendTransactionsPacket(String transactionSet, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("UNCONFIRMED_TRANSACTION_SET " + transactionSet);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void encryptAndSendPacket(SignedMessage message, ConnectedNode connection) throws Exception {

        if (connection.getEcPubKey() != null) {

            byte[] cleartext = Utils.serialize(message);

            EncryptedMessage encryptedMessage = EncryptionUtils.encrypt(cleartext, connection.getEcPubKey());

            sendPacket(encryptedMessage, connection.getIp());

        } else {

            sendPacket(message, connection.getIp());
        }
    }

    public static void sendPacket(Serializable message, InetAddress ip) throws Exception {

        byte[] packet = Utils.serialize(message);

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

        void onPacketReceived(byte[] bytes);
    }

    public static void runWhenPacketReceived(PacketReceivedListener listener) {

        Thread thread = new Thread() {

            @Override
            public void run() {

                try {

                    byte[] bytes = receivePacket();

                    listener.onPacketReceived(bytes);

                } catch (Exception ex) {
                    System.out.println("Exception in NetworkAdapter.runWhenPacketReceived");
                }

                runWhenPacketReceived(listener);
            }
        };

        thread.start();
    }
}
