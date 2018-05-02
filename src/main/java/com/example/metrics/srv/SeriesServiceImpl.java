package com.example.metrics.srv;

import com.example.metrics.AppProperties;
import com.example.metrics.wsp.entities.Series;
import com.example.metrics.wsp.service.Filter;
import com.example.metrics.wsp.service.WspReader;
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

        Filter filter = Filter.Builder.newInstance()
                .timestampPredicate(Filter.ignoreZeroIntPredicate)
                .secondsPerPointPredicate(s -> s == appProperties.getSecondsPerPoint())
                .datapointComparator(Filter.ascDatapointComparator)
                .build();

        return appProperties.getMetricsToAggregateIds().stream()
                .map(metricId -> appProperties.getInputDataRootPath() + metricId + DATABASE_FILE_EXTENSION)
                .map(Paths::get)
                .map(path -> wspReader.getSeriesByWspFilePath(path, filter))
                .collect(Collectors.toList());
    }
}
