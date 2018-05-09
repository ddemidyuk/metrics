package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.AbstractInterval;
import com.example.metrics.interval.entities.Interval;

import java.util.List;

class FragmentedInterval extends AbstractInterval {

    private List<Interval> intervals;

    private FragmentedInterval(Builder builder) {
        super(builder);
        this.intervals = builder.intervals;
    }

    //todo
    public double[] getValues() {
        /*double[] values = new double[getCountOfTimestamps()];
        final Integer  i = 0;
        intervals.stream()
                .map(Interval::getValues)
                .flatMap(v -> Arrays.stream(values))
                .forEach(value ->{ values[i]= value;});
        return values;*/
        throw new UnsupportedOperationException();
    }

    public Double getValue(int timestamp) {
        Double value = null;
        for(Interval interval : intervals){
            value = interval.getValue(timestamp);
            if(value != null) break;
        }
        return value;
    }

    public static final class Builder extends AbstractIntervalBuilder<Builder> {

        private List<Interval> intervals;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder intervals(List<Interval> intervals) {
            this.intervals = intervals;
            return this;
        }

        public FragmentedInterval build() {
            return new FragmentedInterval(this);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
