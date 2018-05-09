package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.Period;

import java.util.Set;

public interface IntervalFactoryParam {
    void addValue(double value);
    void reset(int startTimestamp);
    int getStartTimestamp();
    int getEndTimestamp();
    double[] getValues();
    int getSecondsPerPoint();
    boolean isAllValuesAreTheSame();
    Set<Period> getPeriodsWithTheSameValues();
}
