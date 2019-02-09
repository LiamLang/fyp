package com.liamlang.fyp.Utils;

import java.security.AlgorithmParameters;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EncryptionUtils {

    private static AlgorithmParameters spec = null; // TODO!
    
    public static KeyPair generateEcKeyPair() {

        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            keyGen.initialize(new ECGenParameterSpec("secp256r1"));

            return keyGen.generateKeyPair();

        } catch (Exception ex) {
            System.out.println("Exception generating KeyPair! EncryptionUtils.generateEcKeyPair");
            System.out.println(ex.getMessage());
            ex.printStackTrace();

            return null;
        }
    }

    public static byte[] encrypt(byte[] plaintext, PublicKey pubKey) {

        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher ecies = Cipher.getInstance("ECIESwithAES-CBC");
            ecies.init(Cipher.ENCRYPT_MODE, pubKey);
            
            // TODO remove!
            spec = ecies.getParameters();

            return ecies.doFinal(plaintext);

        } catch (Exception ex) {

            System.out.println("Exception in EncryptionUtils.encrypt:");
            System.out.println(ex.getMessage());
            ex.printStackTrace();

            return new byte[]{};
        }
    }

    public static byte[] decrypt(byte[] ciphertext, PrivateKey privKey) {

        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher ecies = Cipher.getInstance("ECIESwithAES-CBC");
            ecies.init(Cipher.DECRYPT_MODE, privKey, spec);

            return ecies.doFinal(ciphertext);

        } catch (Exception ex) {

            System.out.println("Exception in EncryptionUtils.decrypt:");
            System.out.println(ex.getMessage());
            ex.printStackTrace();

            return new byte[]{};
        }
    }
}
