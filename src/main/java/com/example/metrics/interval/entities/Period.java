package com.example.metrics.interval.entities;

public class Period {
    private int startTimestamp;
    private int endTimestamp;
    private int secondsPerPoint;

    public Period(int startTimestamp, int endTimestamp, int secondsPerPoint) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.secondsPerPoint = secondsPerPoint;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public int getEndTimestamp() {
        return endTimestamp;
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }
}
