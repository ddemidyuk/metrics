package com.example.metrics.entity.csv;

import java.util.List;

public class CsvHeader {
    List<String> seriesId;

    public CsvHeader(List<String> seriesId) {
        this.seriesId = seriesId;
    }

    public List<String> getSeriesId() {
        return seriesId;
    }
}
