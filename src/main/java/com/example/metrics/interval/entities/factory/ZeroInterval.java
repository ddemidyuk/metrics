package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.AbstractInterval;

import java.util.Arrays;

class ZeroInterval extends AbstractInterval {

    private static final double VALUE = 0d;

    private ZeroInterval(Builder builder) {
        super(builder);
    }

    public double[] getValues() {
        double[] values = new double[getCountOfTimestamps()];
        Arrays.fill(values, VALUE);
        return values;
    }

    public Double getValue(int timestamp) {
        if (isContainsTimestamp(timestamp)) {
            return VALUE;
        }
        return null;
    }

    public static final class Builder extends AbstractIntervalBuilder<Builder> {

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public ZeroInterval build() {
            return new ZeroInterval(this);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
