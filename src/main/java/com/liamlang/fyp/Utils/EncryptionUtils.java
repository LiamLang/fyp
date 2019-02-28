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
import org.bouncycastle.jce.spec.IESParameterSpec;

public class EncryptionUtils {

    public static KeyPair generateEcKeyPair() {

        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            keyGen.initialize(new ECGenParameterSpec("secp128r1"));

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
            Cipher ecies = Cipher.getInstance("ECIESwithAES-CBC", "BC");
            ecies.init(Cipher.ENCRYPT_MODE, pubKey);

            AlgorithmParameters params = ecies.getParameters();
            IESParameterSpec paramSpec = params.getParameterSpec(IESParameterSpec.class);
                       
            byte[] ciphertext = ecies.doFinal(cleartext);

            return new EncryptedMessage(ciphertext, paramSpec.getDerivationV(), paramSpec.getEncodingV(), paramSpec.getMacKeySize(),
                    paramSpec.getCipherKeySize(), paramSpec.getNonce(), paramSpec.getPointCompression());

        } catch (Exception ex) {

            System.out.println("Exception in EncryptionUtils.encrypt:");
            System.out.println(ex.getMessage());
            ex.printStackTrace();

            return null;
        }
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] derivation, byte[] encoding, int macKeySize, 
            int cipherKeySize, byte[] nonce, boolean usePointCompression, PrivateKey privKey) {

        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher ecies = Cipher.getInstance("ECIESwithAES-CBC", "BC");
            AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance("IES");
            IESParameterSpec paramSpec = new IESParameterSpec(derivation, encoding, macKeySize, cipherKeySize, nonce, usePointCompression);
            algorithmParameters.init(paramSpec);
            ecies.init(Cipher.DECRYPT_MODE, privKey, algorithmParameters);

            return ecies.doFinal(ciphertext);

        } catch (Exception ex) {

            System.out.println("Exception in EncryptionUtils.decrypt:");
            System.out.println(ex.getMessage());
            ex.printStackTrace();

            return new byte[]{};
        }
    }
}
