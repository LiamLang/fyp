package com.liamlang.fyp.Utils;

import java.io.UnsupportedEncodingException;
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
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return new byte[0];
        }
    }
    
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
}
