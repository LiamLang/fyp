package com.liamlang.fyp.Model;

import java.io.Serializable;
import java.security.PublicKey;

public class TrustedSignee implements Serializable {

    private final String name;
    private final PublicKey key;

    public TrustedSignee(PublicKey key, String name) {
        this.key = key;
        this.name = name;
    }

    public PublicKey getPubkey() {
        return key;
    }
    
    public String getName() {
        return name;
    }
}
