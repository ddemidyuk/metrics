package com.example.metrics.wsp.service;

import java.nio.file.Path;

public class Params {
    private final Path metricPath;
    private final String metricId;
    private final Filter filter;

    public Params(Path metricPath, String metricId, Filter filter) {
        this.metricPath = metricPath;
        this.metricId = metricId;
        this.filter = filter;
    }

    public Path getMetricPath() {
        return metricPath;
    }

    public Filter getFilter() {
        return filter;
    }

    public String getMetricId() {
        return metricId;
    }
}
