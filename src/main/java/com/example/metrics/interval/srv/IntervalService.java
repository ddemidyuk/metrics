package com.example.metrics.interval.srv;

import com.example.metrics.interval.entities.Metric;

import java.util.List;


public interface IntervalService {
    List<Metric> getMetrics();
}
