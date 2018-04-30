package com.example.metrics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {
    @Value("${timestamp.to.aggregate.accuracy}")
    private String timestampToAggregateAccuracy;

    @Value("${non.null.metrics.per.timestamp.threshold}")
    private String nonNullMetricsPerTimestampThreshold;

    @Value("${input.data.root.path}")
    private String inputDataRootPath;

    @Value("${output.csv.path}")
    private String outputCsvPath;

    public String getTimestampToAggregateAccuracy() {
        return timestampToAggregateAccuracy;
    }

    public String getNonNullMetricsPerTimestampThreshold() {
        return nonNullMetricsPerTimestampThreshold;
    }

    public String getInputDataRootPath() {
        return inputDataRootPath;
    }

    public String getOutputCsvPath() {
        return outputCsvPath;
    }
}
