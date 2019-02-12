package com.liamlang.fyp.Model;

import java.io.Serializable;
import java.net.InetAddress;
import java.security.PublicKey;

public class ConnectedNode implements Serializable {

    private InetAddress ip;
    private PublicKey ecPubKey;

    public ConnectedNode(InetAddress ip) {
        this.ip = ip;
        this.ecPubKey = null;
    }

    public ConnectedNode(InetAddress ip, PublicKey ecPubKey) {
        this.ip = ip;
        this.ecPubKey = ecPubKey;
    }

    public InetAddress getIp() {
        return ip;
    }

    public PublicKey getEcPubKey() {
        return ecPubKey;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void setEcPubKey(PublicKey ecPubKey) {
        this.ecPubKey = ecPubKey;
    }
}
