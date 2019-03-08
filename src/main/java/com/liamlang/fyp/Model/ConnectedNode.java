package com.liamlang.fyp.Model;

import java.io.Serializable;
import java.net.InetAddress;
import java.security.PublicKey;

public class ConnectedNode implements Serializable {

    private InetAddress ip;
    private int port;
    private PublicKey ecPubKey;

    public ConnectedNode(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        this.ecPubKey = null;
    }

    public ConnectedNode(InetAddress ip, int port, PublicKey ecPubKey) {
        this.ip = ip;
        this.port = port;
        this.ecPubKey = ecPubKey;
    }

    public InetAddress getIp() {
        return ip;
    }
    
    public int getPort() {
        return port;
    }

    public PublicKey getEcPubKey() {
        return ecPubKey;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
    
    public void setPort(int port) {
        this.port = port;
    }

    public void setEcPubKey(PublicKey ecPubKey) {
        this.ecPubKey = ecPubKey;
    }
}
