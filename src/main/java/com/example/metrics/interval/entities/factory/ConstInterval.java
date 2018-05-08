package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.AbstractInterval;

import java.util.Arrays;

class ConstInterval extends AbstractInterval {

    private final double value;

    private ConstInterval(Builder builder) {
        super(builder);
        this.value = builder.value;
    }

    public double[] getValues() {
        double[] values = new double[getCountOfTimestamps()];
        Arrays.fill(values, value);
        return values;
    }

    public Double getValue(int timestamp) {
        if (isContainsTimestamp(timestamp)) {
            return value;
        }
        return null;
    }

    public static final class Builder extends AbstractInterval.AbstractIntervalBuilder<Builder> {


        private double value;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public ConstInterval build() {
            return new ConstInterval(this);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
