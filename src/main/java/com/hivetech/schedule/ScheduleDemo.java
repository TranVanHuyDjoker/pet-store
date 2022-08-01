package com.hivetech.schedule;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleDemo {
    public static void main(String[] args) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    // Công việc mất 2s
                    Thread.sleep(2000);
                    System.out.println("This task cosume 2s " + LocalTime.now());
                } catch (InterruptedException e) {
                }
            }
        };
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        executorService.scheduleAtFixedRate(timerTask,0,1, TimeUnit.SECONDS);
    }
}
