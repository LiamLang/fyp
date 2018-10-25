package com.liamlang.fyp.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static byte[] sha256(byte[] input) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return messageDigest.digest(input);
        } catch (NoSuchAlgorithmException ex) {
            return new byte[]{};
        }
    }

    public static byte[] sha256(Serializable input) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(input);
        out.flush();
        byte[] bytes = bos.toByteArray();
        bos.close();

        return sha256(bytes);
    }
}
