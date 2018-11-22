package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.SignatureUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.PublicKey;


public class SignedMessage implements Serializable {
    
    private String message;
    private byte[] signature;
    private PublicKey pub;
    
    public SignedMessage(String message) {
        this.message = message;
    }
    
    public void sign(KeyPair keyPair) {
        this.signature = SignatureUtils.sign(Utils.toByteArray(message), keyPair.getPrivate());
        this.pub = keyPair.getPublic();
        System.out.println("Signed a message with sig length = " + Integer.toString(signature.length));
    }
    
    public String getMessage() {
        return message;
    }
    
    public PublicKey getPublicKey() {
        return pub;
    }
    
    public boolean verify() {
        if (SignatureUtils.verify(Utils.toByteArray(message), signature, pub)) {
            System.out.println("Verified a message signature");
            return true;
        } else {
            System.out.println("Failed to verify a message signature!");
            return false;
        }
    }
}
