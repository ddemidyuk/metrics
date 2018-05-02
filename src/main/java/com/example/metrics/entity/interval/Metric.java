package com.example.metrics.entity.interval;

import java.util.List;

public class Metric {
    private final String MetricId;
    private final List<Interval> intervals;

    public Metric(String metricId, List<Interval> intervals) {
        MetricId = metricId;
        this.intervals = intervals;
    }

    public String getMetricId() {
        return MetricId;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }
}
