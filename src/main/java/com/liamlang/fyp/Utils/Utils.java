package com.liamlang.fyp.Utils;

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
}
