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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return startTimestamp == period.startTimestamp &&
                endTimestamp == period.endTimestamp &&
                secondsPerPoint == period.secondsPerPoint;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + startTimestamp;
        result = prime * result + endTimestamp;
        result = prime * result + secondsPerPoint;
        return result;
    }
}
