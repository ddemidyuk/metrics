package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.Period;

import java.util.Set;
import java.util.function.BiFunction;

public interface IntervalFactoryParam {
    String getMetricId();
    void addValue(double value);
    void reset(int startTimestamp);
    int getStartTimestamp();
    int getEndTimestamp();
    double[] getValues();
    int getSecondsPerPoint();
    boolean isAllValuesAreTheSame();
    Set<Period> getPeriodsWithTheSameValues();
    BiFunction<String, Period, double[]> getFunctionForRestoreFromDb();
}
