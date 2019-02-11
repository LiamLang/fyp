package com.liamlang.fyp.Model;

import com.liamlang.fyp.Utils.EncryptionUtils;
import java.io.Serializable;
import java.security.AlgorithmParameters;
import java.security.PrivateKey;

public class EncryptedMessage implements Serializable {

    private final byte[] ciphertext;
    private final AlgorithmParameters params;

    public EncryptedMessage(byte[] ciphertext, AlgorithmParameters params) {

        this.ciphertext = ciphertext;
        this.params = params;
    }

    public byte[] decrypt(PrivateKey privKey) {

        return EncryptionUtils.decrypt(ciphertext, params, privKey);
    }
}
