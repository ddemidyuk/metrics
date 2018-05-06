package com.example.metrics.interval.entities;

import java.util.List;

public class Metric {
    private final String id;
    private final List<Interval> intervals;

    public Metric(String id, List<Interval> intervals) {
        this.id = id;
        this.intervals = intervals;
    }

    public String getId() {
        return id;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public Double getValue(int timestamp) {
        Double value = null;
        for (Interval interval : intervals) {
            value = interval.getValue(timestamp);
            if (value != null){
                break;
            }
        }
        return value;
    }
}
