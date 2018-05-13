package com.example.metrics.wsp.service;

import com.example.metrics.wsp.entities.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class WspReaderQueue {
    @Autowired
    private WspReader wspReader;

    private BlockingQueue<Params> inQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Series> outQueue = new LinkedBlockingQueue<>();

    private Thread createSeriesThread;

    private Runnable createSeriesTask = () -> {
        try {
            while (true) {
                Params params = inQueue.take();
                Series series = wspReader.getSeries(params);
                outQueue.offer(series);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    public WspReaderQueue() {
        createSeriesThread = new Thread(createSeriesTask);
        createSeriesThread.setDaemon(true);
        createSeriesThread.start();
    }

    public void offer(Params params) {
        inQueue.offer(params);
    }


    public Series take() {
        try {
            return outQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
