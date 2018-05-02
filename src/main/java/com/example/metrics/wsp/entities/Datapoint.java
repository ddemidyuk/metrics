package com.example.metrics.wsp.entities;

import java.util.Date;

public class Datapoint {
    private final int timestamp;
    private final double value;

    public Datapoint(int timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    public Date getDate() {
        return new Date(timestamp * 1000L);
    }
}
