package com.example.metrics.interval.entities;

import java.util.function.BiFunction;

public abstract class AbstractStorableInterval extends AbstractInterval implements StorableInterval {
    protected String metricId;
    protected BiFunction<String, Period, double[]> functionForRestoreFromDb;


    public AbstractStorableInterval(AbstractIntervalBuilder builder) {
        super(builder);
    }
}
