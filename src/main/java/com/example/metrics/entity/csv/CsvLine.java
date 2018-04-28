package com.example.metrics.entity.csv;

import java.util.Date;
import java.util.List;

public class CsvLine {
    Date timeStamp;
    List<Double> values;

    public CsvLine(Date timeStamp, List<Double> values) {
        this.timeStamp = timeStamp;
        this.values = values;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public List<Double> getValues() {
        return values;
    }
}
