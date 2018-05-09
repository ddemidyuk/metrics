package com.example.metrics.interval.entities;

public interface Interval {
     double[] getValues();
     Double getValue(int timestamp);
     Period getPeriod();
}
