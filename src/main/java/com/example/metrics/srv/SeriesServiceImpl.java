package com.example.metrics.srv;

import com.example.metrics.AppProperties;
import com.example.metrics.entity.wsp.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeriesServiceImpl implements SeriesService {

    private static final String DATABASE_FILE_EXTENSION = ".wsp";

    private WspReader wspReader;

    private AppProperties appProperties;

    @Autowired
    public SeriesServiceImpl(WspReader wspReader, AppProperties appProperties) {
        this.wspReader = wspReader;
        this.appProperties = appProperties;
    }

    public List<Series> getSeriesListBySeriesIds() throws IOException {
        Filter filter = new Filter(appProperties.getTimestampToAggregateAccuracy());
        return appProperties.getMetricsToAggregateIds().stream()
                .map(metricId -> appProperties.getInputDataRootPath() + metricId + DATABASE_FILE_EXTENSION)
                .map(Paths::get)
                .map(path -> wspReader.getSeriesByWspFilePath(path, filter))
                .collect(Collectors.toList());
    }
}
