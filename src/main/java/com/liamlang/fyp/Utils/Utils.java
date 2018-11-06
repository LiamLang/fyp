package com.liamlang.fyp.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static void scheduleRepeatingTask(long delayMillis, Runnable task) {

        Runnable repeatingTask = new Runnable() {
            @Override
            public void run() {
                task.run();
                scheduleRepeatingTask(delayMillis, task);
            }
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(repeatingTask, delayMillis, TimeUnit.MILLISECONDS);
    }

    public static byte[] toByteArray(String str) {
        return str.getBytes(StandardCharsets.ISO_8859_1);
    }

    public static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    // From Stack Overflow
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] serialize(Serializable input) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(input);
            out.flush();
            byte[] bytes = bos.toByteArray();
            bos.close();
            return bytes;
        } catch (IOException ex) {
            System.out.println("Exception serializing object");
            return new byte[]{};
        }
    }

    public static Serializable deserialize(byte[] input) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(input);
            ObjectInputStream in = new ObjectInputStream(bis);
            Serializable obj = (Serializable) in.readObject();
            in.close();
            bis.close();
            return obj;
        } catch (Exception ex) {
            System.out.println("Exception deserializing object");
            return null;
        }
    }
}
