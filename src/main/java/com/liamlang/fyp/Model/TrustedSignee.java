package com.liamlang.fyp.Model;

import java.security.PublicKey;

public class TrustedSignee {

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
