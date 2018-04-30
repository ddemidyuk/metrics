package com.example.metrics.entity.wsp;

import java.util.Date;

public class Datapoint implements Comparable {
    private final int timestamp;
    private final Double value;

    public Datapoint(int timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        return this.timestamp - ((Datapoint) o).getTimestamp();
    }

    public Date getDate() {
        return new Date(timestamp * 1000L);
    }
}
