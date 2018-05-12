package com.example.metrics.interval.entities;

import java.util.List;

public class Metric {
    private final String id;
    private final List<Interval> intervals;
    private final IntervalsForRestoreQueue intervalsForRestoreQueue;

    public Metric(String id, List<Interval> intervals) {
        this.id = id;
        //todo remove this
        Interval interval = intervals.get(0);
        this.intervals = intervals;
        this.intervalsForRestoreQueue = IntervalsForRestoreQueue.getInstance();
        if(interval instanceof StorableInterval ){
            intervalsForRestoreQueue.offer((StorableInterval) interval);
        }
    }

    public String getId() {
        return id;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

/*    public Double getValue(int timestamp) {
        Double value = null;
        for (Interval interval : intervals) {
            value = interval.getValue(timestamp);
            if (value != null){
                break;
            }
        }
        return value;
    }*/

    public Double getValue(int timestamp) {
        if (intervals.isEmpty()) return null;

        Interval interval = intervals.get(0);
        Double value = interval.getValue(timestamp);

        if (interval.getPeriod().getEndTimestamp() == timestamp && intervals.size() > 1) {
            Interval nextInterval = intervals.get(1);
            if(nextInterval instanceof StorableInterval){
                intervalsForRestoreQueue.offer((StorableInterval) nextInterval);
            }
            intervals.remove(0);
        }

        return value;
    }

}
