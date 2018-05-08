package com.example.metrics.interval.entities;

public interface Interval {
     int getStartTimestamp();
     int getEndTimestamp();
     double[] getValues();
     Double getValue(int timestamp);
     int getSecondsPerPoint();
     Period getPeriod();
}
