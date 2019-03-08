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
import java.security.KeyPair;

public class NetworkAdapter {

    static final int MAX_PACKET_SIZE = 1000000;

    public static void sendSyncPacket(String myIp, int port, int height, int numConnections, String unconfirmedTransactionSetHash, String myEcPubKey, ConnectedNode connection, KeyPair myDsaKeyPair, String myName, String isSupernode) throws Exception {

        SignedMessage m = new SignedMessage("SYNC " + myIp + " " + Integer.toString(port) + " " + Integer.toString(height) + " " + Integer.toString(numConnections) + " " + unconfirmedTransactionSetHash + " " + isSupernode + " " + myEcPubKey);
        m.sign(myDsaKeyPair, myName);

        System.out.println("Sending unencrypted sync to " + connection.getIp().toString() + ": " + m.getMessage() + "\n");

        sendPacket(m, connection.getIp(), connection.getPort());
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

    public static void sendComponentHashRequest(String myIp, int port, String hash, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("COMPONENT_HASH_REQUEST " + myIp + " " + Integer.toString(port) + " " + hash);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void sendComponentInfoRequest(String myIp, int port, String info, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("COMPONENT_INFO_REQUEST " + myIp + " " + Integer.toString(port) + " " + info);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void sendShowComponentRequest(String component, String confirmationStatus, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("SHOW_COMPONENT_REQUEST " + confirmationStatus + " " + component);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void sendCreateComponentTransactionRequest(String info, String quantity, String ownerName, String ownerPubKey, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("CREATE_COMPONENT_TRANSACTION_REQUEST " + info + " " + quantity + " " + ownerName + " " + ownerPubKey);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void sendAssembleComponentsTransactionRequest(String parentHash, String childHash, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("ASSEMBLE_COMPONENTS_TRANSACTION_REQUEST " + parentHash + " " + childHash);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void sendDisassembleComponentsTransactionRequest(String parentHash, String childHash, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("DISASSEMBLE_COMPONENTS_TRANSACTION_REQUEST " + parentHash + " " + childHash);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void sendChangeOwnershipTransactionRequest(String hash, String newOwner, String signature, ConnectedNode connection, KeyPair myDsaKeyPair, String myName) throws Exception {

        SignedMessage m = new SignedMessage("CHANGE_OWNERSHIP_TRANSACTION_REQUEST " + hash + " " + newOwner + " " + signature);
        m.sign(myDsaKeyPair, myName);

        encryptAndSendPacket(m, connection);
    }

    public static void encryptAndSendPacket(SignedMessage message, ConnectedNode connection) throws Exception {

        if (connection.getEcPubKey() != null) {

            byte[] cleartext = Utils.serialize(message);

            EncryptedMessage encryptedMessage = EncryptionUtils.encrypt(cleartext, connection.getEcPubKey());

            System.out.println("Sending [encrypted] to " + connection.getIp().toString() + ": " + message.getMessage() + "\n");

            sendPacket(encryptedMessage, connection.getIp(), connection.getPort());

        } else {

            System.out.println("Sending [unencrypted] to " + connection.getIp().toString() + ": " + message.getMessage() + "\n");

            sendPacket(message, connection.getIp(), connection.getPort());
        }
    }

    public static void sendPacket(Serializable message, InetAddress ip, int port) throws Exception {

        byte[] packet = Utils.serialize(message);

        DatagramSocket datagramSocket = new DatagramSocket();

        DatagramPacket datagramPacket = new DatagramPacket(packet, packet.length, ip, port);

        datagramSocket.send(datagramPacket);

        datagramSocket.close();
    }

    public static byte[] receivePacket(int port) throws Exception {

        DatagramSocket datagramSocket = new DatagramSocket(port);

        byte[] buf = new byte[MAX_PACKET_SIZE];

        DatagramPacket datagramPacket = new DatagramPacket(buf, MAX_PACKET_SIZE);

        datagramSocket.receive(datagramPacket);

        datagramSocket.close();

        return datagramPacket.getData();
    }

    public interface PacketReceivedListener {

        void onPacketReceived(byte[] bytes);
    }

    public static void runWhenPacketReceived(int port, PacketReceivedListener listener) {

        Thread thread = new Thread() {

            @Override
            public void run() {

                try {

                    byte[] bytes = receivePacket(port);

                    listener.onPacketReceived(bytes);

                } catch (Exception ex) {
                    System.out.println("Exception in NetworkAdapter.runWhenPacketReceived");
                }

                runWhenPacketReceived(port, listener);
            }
        };

        thread.start();
    }
}
