package com.example.metrics.interval.entities;

public interface Interval {
     int getStartTimestamp();
     int getEndTimestamp();
     double[] getValues();
     Double getValue(int timestamp);
     int getSecondsPerPoint();
     //todo move to abstract class
     default Period getPeriod(){
          return new Period(getStartTimestamp(), getEndTimestamp(), getSecondsPerPoint());
     }
}
