package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.Interval;

class SimpleInterval implements Interval {
    private final int startTimestamp;
    private final int endTimestamp;
    private final double[] values;
    private final int secondsPerPoint;

    private SimpleInterval(Builder builder) {
        this.startTimestamp = builder.startTimestamp;
        this.values = builder.values;
        this.secondsPerPoint = builder.secondsPerPoint;
        this.endTimestamp = this.startTimestamp + this.values.length * this.secondsPerPoint;

    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public double[] getValues() {
        return values;
    }

    public Double getValue(int timestamp) {
        if (timestamp < startTimestamp || timestamp > getEndTimestamp()) {
            return null;
        }
        return values[(timestamp - startTimestamp) / secondsPerPoint];
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public int getEndTimestamp() {
        return endTimestamp;
    }

    public static final class Builder {
        private int startTimestamp;
        private double[] values;
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

        public Builder values(double[] values) {
            this.values = values;
            return this;
        }

        public Builder secondsPerPoint(int secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return this;
        }

        public SimpleInterval build() {
            return new SimpleInterval(this);
        }
    }
}