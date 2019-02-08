package com.liamlang.fyp.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

public class Utils {

    public static void scheduleRepeatingTask(long delayMillis, Runnable task) {

        Runnable repeatingTask = new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                    scheduleRepeatingTask(delayMillis, task);
                } catch (Exception ex) {
                    System.out.println("Error in repeating task, stopping!");
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(repeatingTask, delayMillis, TimeUnit.MILLISECONDS);
    }

    public static String toHumanReadableTime(long timestamp) {

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        return sdf.format(timestamp);
    }

    public static void showOkPopup(String text) {
        JOptionPane.showMessageDialog(null, text);
    }

    public static boolean showYesNoPopup(String text) {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, text, "", dialogButton);
        return dialogResult == JOptionPane.YES_OPTION;
    }

    public static byte[] toByteArray(String str) {
        return str.getBytes(StandardCharsets.ISO_8859_1);
    }

    public static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    // From Stack Overflow
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String toHexString(byte[] bytes) {
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
            ex.printStackTrace();
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
            ex.printStackTrace();
            return null;
        }
    }
}
