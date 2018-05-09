package com.example.metrics.interval.entities;

public abstract class AbstractStorableInterval extends AbstractInterval implements StorableInterval {

    public AbstractStorableInterval(AbstractIntervalBuilder builder) {
        super(builder);
    }
}
