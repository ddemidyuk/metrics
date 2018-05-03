package com.example.metrics.interval.entities;

import java.util.Arrays;

public class ZeroInterval implements Interval {
    private final int startTimestamp;
    private final int endTimestamp;
    private final int secondsPerPoint;

    private ZeroInterval(Builder builder) {
        this.startTimestamp = builder.startTimestamp;
        this.endTimestamp = builder.endTimestamp;
        this.secondsPerPoint = builder.secondsPerPoint;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public double[] getValues() {
        double[] values = new double[(startTimestamp - endTimestamp) / secondsPerPoint];
        Arrays.fill(values, 0d);
        return values;
    }

    public Double getValue(int timestamp) {
        if (timestamp < startTimestamp || timestamp > endTimestamp) {
            return null;
        }
        return 0d;
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public int getEndTimestamp() {
        return endTimestamp;
    }

    public static final class Builder {
        private int startTimestamp;
        private int endTimestamp;
        private int secondsPerPoint;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder startTimestamp(int startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public Builder endTimestamp(int endTimestamp) {
            this.endTimestamp = endTimestamp;
            return this;
        }

        public Builder secondsPerPoint(int secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return this;
        }

        public ZeroInterval build() {
            return new ZeroInterval(this);
        }
    }
}