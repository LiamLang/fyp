package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.EncryptionUtils;
import java.io.Serializable;
import java.security.PrivateKey;

public class EncryptedMessage implements Serializable {

    private final byte[] ciphertext;

    private final byte[] derivation;
    private final byte[] encoding;
    private final int macKeySize;
    private final int cipherKeySize;
    private final byte[] nonce;
    private final boolean usePointCompression;

    public EncryptedMessage(byte[] ciphertext, byte[] derivation, byte[] encoding, 
            int macKeySize, int cipherKeySize, byte[] nonce, boolean usePointCompression) {

        this.ciphertext = ciphertext;
        
        this.derivation = derivation;
        this.encoding = encoding;
        this.macKeySize = macKeySize;
        this.cipherKeySize = cipherKeySize;
        this.nonce = nonce;
        this.usePointCompression = usePointCompression;
    }

    public byte[] decrypt(PrivateKey privKey) {

        return EncryptionUtils.decrypt(ciphertext, derivation, encoding, macKeySize, cipherKeySize, nonce, usePointCompression, privKey);
    }
}
