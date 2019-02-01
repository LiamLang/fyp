package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.SignatureUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.PublicKey;


public class SignedMessage implements Serializable {
    
    private final String message;
    private String signee;
    private byte[] signature;
    private PublicKey pub;
    
    public SignedMessage(String message) {
        this.message = message;
    }
    
    public void sign(KeyPair keyPair, String signee) {
        this.signature = SignatureUtils.sign(Utils.toByteArray(message), keyPair.getPrivate());
        this.pub = keyPair.getPublic();
        this.signee = signee;
    }
    
    public String getMessage() {
        return message;
    }
    
    public PublicKey getPublicKey() {
        return pub;
    }
    
    public String getSignee() {
        return signee;
    }
    
    public boolean verify() {
        if (SignatureUtils.verify(Utils.toByteArray(message), signature, pub)) {
            return true;
        } else {
            System.out.println("Failed to verify a message signature!");
            return false;
        }
    }
}
