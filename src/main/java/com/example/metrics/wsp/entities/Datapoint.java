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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Datapoint datapoint = (Datapoint) o;
        return timestamp == datapoint.timestamp &&
                Double.compare(datapoint.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return timestamp;
    }
}
