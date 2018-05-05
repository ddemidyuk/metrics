package com.example.metrics.interval.entities.factory;

import java.util.Arrays;

public class IntervalFactoryParam {
    private int startTimestamp;
    private double[] buffer;
    private int countValues;
    private int secondsPerPoint;
    private boolean allValuesAreTheSame;
    private boolean theLastValuesAreTheSame;
    private int[] endsOfSubIntervals;
    private int countSubIntervals;

    private static final int MIN_COUNT_OF_THE_SAME_VALUE_TO_COMBINE_IN_SUBINTERVAL = 10;

    public IntervalFactoryParam(Builder builder) {
        this.buffer = new double[builder.bufferSize];
        this.secondsPerPoint = builder.secondsPerPoint;
        this.endsOfSubIntervals = new int[builder.bufferSize / MIN_COUNT_OF_THE_SAME_VALUE_TO_COMBINE_IN_SUBINTERVAL + 1];
        reset(builder.startTimestamp);
    }

    public void addValue(double value) {
        if (allValuesAreTheSame && countValues != 0 && buffer[countValues - 1] != value) {
            allValuesAreTheSame = false;
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
        this.allValuesAreTheSame = true;
        this.theLastValuesAreTheSame = false;
        this.countSubIntervals = 0;
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public boolean isAllValuesAreTheSame() {
        return allValuesAreTheSame;
    }

    public boolean canCreateNewIntervalOfTheSameValues(double value) {
        return allValuesAreTheSame && countValues >= MIN_COUNT_OF_THE_SAME_VALUE_TO_COMBINE_IN_SUBINTERVAL
                && value != buffer[0];
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
