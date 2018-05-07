package com.example.metrics.interval.entities;

public interface StorableInterval extends Interval {
    void storeValues(String folderPath);
    void restoreValues();
}
