package com.example.metrics.wsp.service;

import com.example.metrics.wsp.entities.Datapoint;

import java.util.Comparator;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;

public class Filter {
    private final IntPredicate timestampPredicate;
    private final DoublePredicate valuePredicate;
    private final IntPredicate secondsPerPointPredicate;
    private final Comparator<Datapoint> datapointComparator;

    private static final IntPredicate ALWAYS_TRUE_INT_PREDICATE = i -> true;
    private static final DoublePredicate ALWAYS_TRUE_DOUBLE_PREDICATE = d -> true;

    public static final Comparator<Datapoint> ASC_DATAPOINT_COMPARATOR = (d1, d2) -> d1.getTimestamp() - d2.getTimestamp();
    public static final Comparator<Datapoint> DESC_DATAPOINT_COMPARATOR = (d1, d2) -> d2.getTimestamp() - d1.getTimestamp();
    public static final IntPredicate SKIP_ZERO_INT_PREDICATE = i -> i != 0;
    public static final DoublePredicate SKIP_ZERO_DOUBLE_PREDICATE = d -> d != 0.0;


    private Filter(Builder builder) {
        this.timestampPredicate = builder.timestampPredicate != null ? builder.timestampPredicate : ALWAYS_TRUE_INT_PREDICATE;
        this.valuePredicate = builder.valuePredicate != null ? builder.valuePredicate : ALWAYS_TRUE_DOUBLE_PREDICATE;
        this.secondsPerPointPredicate = builder.secondsPerPointPredicate != null ? builder.secondsPerPointPredicate : ALWAYS_TRUE_INT_PREDICATE;
        this.datapointComparator = builder.datapointComparator;
    }
    
    public IntPredicate getTimestampPredicate() {
        return timestampPredicate;
    }

    public DoublePredicate getValuePredicate() {
        return valuePredicate;
    }

    public IntPredicate getSecondsPerPointPredicate() {
        return secondsPerPointPredicate;
    }

    public Comparator<Datapoint> getDatapointComparator() {
        return datapointComparator;
    }

    public static final class Builder {

        private IntPredicate timestampPredicate;
        private DoublePredicate valuePredicate;
        private IntPredicate secondsPerPointPredicate;
        private Comparator<Datapoint> datapointComparator;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }
        

        public Builder timestampPredicate(IntPredicate timestampPredicate) {
            this.timestampPredicate = timestampPredicate;
            return this;
        }

        public Builder valuePredicate(DoublePredicate valuePredicate) {
            this.valuePredicate = valuePredicate;
            return this;
        }

        public Builder secondsPerPointPredicate(IntPredicate secondsPerPointPredicate) {
            this.secondsPerPointPredicate = secondsPerPointPredicate;
            return this;
        }

        public Builder datapointComparator(Comparator<Datapoint> datapointComparator) {
            this.datapointComparator = datapointComparator;
            return this;
        }

        public Filter build() {
            return new Filter(this);
        }
    }
}
