package com.liamlang.fyp.Utils;

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
}
