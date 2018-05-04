package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.Interval;

public interface IntervalFactory {
    Interval createInterval(IntervalFactoryParam param);
}
