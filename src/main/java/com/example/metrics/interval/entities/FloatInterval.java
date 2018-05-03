package com.example.metrics.interval.entities;

import java.util.Arrays;

public class FloatInterval implements Interval {
    private final int startTimestamp;
    private final float[] values;
    private final int secondsPerPoint;

    private FloatInterval(Builder builder) {
        this.startTimestamp = builder.startTimestamp;
        this.secondsPerPoint = builder.secondsPerPoint;
        float[] floatValues = new float[builder.values.length];
        for(int i = 0; i<floatValues.length; i++){
            floatValues[i] = (float) builder.values[i];
        }
        this.values = floatValues;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public double[] getValues() {
        double[] doubleValues = new double[values.length];
        Arrays.setAll(doubleValues, i -> (double) values[i]);
        return doubleValues;
    }

    public Double getValue(int timestamp) {
        if (timestamp < startTimestamp || timestamp > getEndTimestamp()) {
            return null;
        }

        return (double) values[(timestamp - startTimestamp) / secondsPerPoint];
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public int getEndTimestamp() {
        return startTimestamp + values.length * secondsPerPoint;
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

        public FloatInterval build() {
            return new FloatInterval(this);
        }
    }
}
