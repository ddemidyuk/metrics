package com.example.metrics.entity.wsp;

import java.util.Date;

public class Datapoint {
    private final Date date;
    private final Double value;

    public Datapoint(Date date, Double value) {
        this.date = date;
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public Double getValue() {
        return value;
    }

}
