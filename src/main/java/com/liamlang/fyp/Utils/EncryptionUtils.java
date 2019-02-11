package com.liamlang.fyp.Utils;

import com.liamlang.fyp.Model.EncryptedMessage;
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

    public static EncryptedMessage encrypt(byte[] cleartext, PublicKey pubKey) {

        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher ecies = Cipher.getInstance("ECIESwithAES-CBC");
            ecies.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] ciphertext = ecies.doFinal(cleartext);
            AlgorithmParameters params = ecies.getParameters();

            return new EncryptedMessage(ciphertext, params);

        } catch (Exception ex) {

            System.out.println("Exception in EncryptionUtils.encrypt:");
            System.out.println(ex.getMessage());
            ex.printStackTrace();

            return null;
        }
    }

    public static byte[] decrypt(byte[] ciphertext, AlgorithmParameters params, PrivateKey privKey) {

        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher ecies = Cipher.getInstance("ECIESwithAES-CBC");
            ecies.init(Cipher.DECRYPT_MODE, privKey, params);

            return ecies.doFinal(ciphertext);

        } catch (Exception ex) {

            System.out.println("Exception in EncryptionUtils.decrypt:");
            System.out.println(ex.getMessage());
            ex.printStackTrace();

            return new byte[]{};
        }
    }
}
