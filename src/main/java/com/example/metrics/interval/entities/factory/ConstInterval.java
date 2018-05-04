package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.Interval;

import java.util.Arrays;

class  ConstInterval implements Interval {
    private final int startTimestamp;
    private final int endTimestamp;
    private final int secondsPerPoint;
    private final double value;

    private ConstInterval(Builder builder) {
        this.startTimestamp = builder.startTimestamp;
        this.endTimestamp = builder.endTimestamp;
        this.secondsPerPoint = builder.secondsPerPoint;
        this.value = builder.value;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public double[] getValues() {
        double[] values = new double[(startTimestamp - endTimestamp) / secondsPerPoint];
        Arrays.fill(values, value);
        return values;
    }

    public Double getValue(int timestamp) {
        if (timestamp < startTimestamp || timestamp > endTimestamp) {
            return null;
        }
        return value;
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
        private double value;

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

        public Builder value(double value) {
            this.value = value;
            return this;
        }
        public Builder secondsPerPoint(int secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return this;
        }

        public ConstInterval build() {
            return new ConstInterval(this);
        }
    }
}
