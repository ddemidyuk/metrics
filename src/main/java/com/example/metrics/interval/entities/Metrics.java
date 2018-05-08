package com.example.metrics.interval.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Metrics implements Iterable<Metric>{
    private List<Metric> metrics = new ArrayList<>();
    private Periods periods = new Periods();


    public void addMetric(Metric metric) {
        metrics.add(metric);
        periods.unitePeriods(metric.getIntervals().stream()
                .map(Interval::getPeriod)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Iterator<Metric> iterator() {
        return metrics.iterator();
    }

    public List<Metric> get() {
        return metrics;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public Periods getPeriods() {
        return periods;
    }
}
