package com.example.metrics.interval.entities;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IntervalsForRestoreQueue {
    private BlockingQueue<StorableInterval> queue = new LinkedBlockingQueue<>();
    private static final IntervalsForRestoreQueue instance = new IntervalsForRestoreQueue();
    private Thread restoreThread;

    private Runnable restoreTask = () -> {
        StorableInterval interval;
        while (true) {
            try {
                queue.take().restoreValues();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private IntervalsForRestoreQueue() {
        restoreThread = new Thread(restoreTask);
        restoreThread.setDaemon(true);
        restoreThread.start();
    }

    public static IntervalsForRestoreQueue getInstance() {
        return instance;
    }

    public void offer(StorableInterval interval) {
        queue.offer(interval);
    }

}
