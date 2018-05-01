package com.example.metrics;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppProperties {
    @Value("${timestamp.to.aggregate.accuracy}")
    private int timestampToAggregateAccuracy;

    @Value("${non.null.metrics.per.timestamp.threshold}")
    private int nonNullMetricsPerTimestampThreshold;

    @Value("${input.data.root.path}")
    private String inputDataRootPath;

    @Value("${output.csv.path}")
    private String outputCsvPath;

    @Value("${metrics.to.aggregate.list.path}")
    private String metricsToAggregateListPath;

    private List<String> metricsToAggregateIds = new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        File csvData = new File(metricsToAggregateListPath);
        CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
        for (CSVRecord csvRecord : parser) {
            metricsToAggregateIds.add(csvRecord.get(0));//todo
        }
    }

    public int getTimestampToAggregateAccuracy() {
        return timestampToAggregateAccuracy;
    }

    public int getNonNullMetricsPerTimestampThreshold() {
        return nonNullMetricsPerTimestampThreshold;
    }

    public String getInputDataRootPath() {
        return inputDataRootPath;
    }

    public String getOutputCsvPath() {
        return outputCsvPath;
    }

    public List<String> getMetricsToAggregateIds() {
        return metricsToAggregateIds;
    }
}
