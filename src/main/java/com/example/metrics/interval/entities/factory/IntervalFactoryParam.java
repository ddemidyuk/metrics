package com.example.metrics.interval.entities.factory;

import java.util.Arrays;

public class IntervalFactoryParam {
    private int startTimestamp;
    private double[] buffer;
    private int countValues;
    private int secondsPerPoint;
    private boolean isAllValuesAreTheSame;

    public IntervalFactoryParam(Builder builder) {
        this.startTimestamp = builder.startTimestamp;
        this.buffer = new double[builder.bufferSize];
        this.secondsPerPoint = builder.secondsPerPoint;
        this.countValues = 0;
        this.isAllValuesAreTheSame = true;
    }

    public void addValue(double value) {
        if (isAllValuesAreTheSame && countValues != 0 && buffer[countValues] != value) {
            isAllValuesAreTheSame = false;
        }
        buffer[countValues++] = value;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public int getEndTimestamp() {
        return startTimestamp + secondsPerPoint * (countValues - 1);
    }

    public double[] getValues() {
        return Arrays.copyOf(buffer, countValues);
    }

    public void reset(int startTimestamp) {
        this.startTimestamp = startTimestamp;
        this.countValues = 0;
        this.isAllValuesAreTheSame = true;
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public boolean isAllValuesAreTheSame() {
        return isAllValuesAreTheSame;
    }


    public static final class Builder {
        private int startTimestamp;
        private int secondsPerPoint;
        private int bufferSize;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder secondsPerPoint(int secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return this;
        }

        public Builder startTimestamp(int startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public IntervalFactoryParam build() {
            return new IntervalFactoryParam(this);
        }
    }
}
