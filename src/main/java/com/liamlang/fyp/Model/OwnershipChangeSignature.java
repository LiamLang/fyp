package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.HashUtils;
import com.liamlang.fyp.Utils.SignatureUtils;
import com.liamlang.fyp.Utils.Utils;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class OwnershipChangeSignature implements Serializable {

    private final String oldComponentHash;
    private final PublicKey newOwnerPubKey;
    
    private final byte[] signature;
    
    public OwnershipChangeSignature(String oldComponentHash, PublicKey newOwnerPubKey, PrivateKey signingKey) {
        this.oldComponentHash = oldComponentHash;
        this.newOwnerPubKey = newOwnerPubKey;
        
        byte[] message = Utils.toByteArray(oldComponentHash + Utils.toHexString(HashUtils.sha256(newOwnerPubKey.getEncoded())));
        
        this.signature = SignatureUtils.sign(message, signingKey);
    }
    
    private byte[] getMessage() {
        return Utils.toByteArray(oldComponentHash + Utils.toHexString(HashUtils.sha256(newOwnerPubKey.getEncoded())));
    }
    
    public boolean verify(Component oldComponent, Component newComponent) {
        return oldComponent.verifyHash() && newComponent.verifyHash() && oldComponent.getHash().equals(oldComponentHash) 
                && newComponent.getOwnerPubKey().equals(newOwnerPubKey) && SignatureUtils.verify(getMessage(), signature, oldComponent.getOwnerPubKey());
    }
    
    public String getOldComponentHash() {
        return oldComponentHash;
    }
    
    public PublicKey newOwnerPubKey() {
        return newOwnerPubKey;
    }
}
