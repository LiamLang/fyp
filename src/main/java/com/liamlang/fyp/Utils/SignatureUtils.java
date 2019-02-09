package com.liamlang.fyp.Utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

public class SignatureUtils {

    public static KeyPair generateDsaKeyPair() {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
            return keyGen.generateKeyPair();

        } catch (Exception ex) {
            System.out.println("Exception generating KeyPair! SignatureUtils.generateDsaKeyPair");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static byte[] sign(byte[] message, PrivateKey priv) {
        try {
            Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
            dsa.initSign(priv);
            dsa.update(message, 0, message.length);
            return dsa.sign();

        } catch (Exception ex) {
            System.out.println("Exception signing! SignatureUtils.sign");
            return new byte[]{};
        }
    }

    public static boolean verify(byte[] message, byte[] signature, PublicKey pub) {
        try {
            Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
            sig.initVerify(pub);
            sig.update(message, 0, message.length);

            return sig.verify(signature);

        } catch (Exception ex) {
            System.out.println("Exception verifying signature! SignatureUtils.verify");
            return false;
        }
    }
}
