package com.example.metrics.interval.srv;

import com.example.metrics.AppProperties;
import com.example.metrics.interval.entities.Interval;
import com.example.metrics.interval.entities.Metric;
import com.example.metrics.interval.entities.factory.IntervalFactory;
import com.example.metrics.interval.entities.factory.IntervalFactoryParam;
import com.example.metrics.wsp.entities.Archive;
import com.example.metrics.wsp.entities.Datapoint;
import com.example.metrics.wsp.entities.Series;
import com.example.metrics.wsp.service.Filter;
import com.example.metrics.wsp.service.Params;
import com.example.metrics.wsp.service.WspReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IntervalServiceImpl implements IntervalService {

    private WspReader wspReader;

    private AppProperties appProperties;

    private IntervalFactory intervalFactory;

    @Autowired
    public IntervalServiceImpl(WspReader wspReader, AppProperties appProperties, IntervalFactory intervalFactory) {
        this.wspReader = wspReader;
        this.appProperties = appProperties;
        this.intervalFactory = intervalFactory;
    }


    public List<Metric> getMetrics(){
        Filter filter = Filter.Builder.newInstance()
                .timestampPredicate(Filter.SKIP_ZERO_INT_PREDICATE)
                .secondsPerPointPredicate(s -> s == appProperties.getSecondsPerPoint())
                .datapointComparator(Filter.ASC_DATAPOINT_COMPARATOR)
                .build();

        return appProperties.getMetricIds().stream()
                .map(metricId -> Params.Builder.newInstance()
                        .seriesId(metricId)
                        .rootPath(appProperties.getInputDataRootPath())
                        .filter(filter)
                        .build())
                .map(wspReader::getSeriesByWspFilePath)
                .map(this::getMetricFromFirstArchiveOfSeries)
                .collect(Collectors.toList());
    }

    //todo переписать говнокод
    private Metric getMetricFromFirstArchiveOfSeries(Series series)
    {
        Archive archive = series.getArchives().get(0);
        int secondsPerPoint = archive.getArchiveInfo().getSecondsPerPoint();
        Set<Datapoint> datapoints = archive.getDatapoints();


        List<Interval> intervals = new ArrayList<>();
        IntervalFactoryParam factoryParam = IntervalFactoryParam.Builder.newInstance()
                .startTimestamp(datapoints.iterator().next().getTimestamp())
                .secondsPerPoint(secondsPerPoint)
                .bufferSize(datapoints.size())
                .build();

        int priorTimestemp = 0;//todo
        int timestamp;

        boolean isFirstStep = true; //todo
        for (Datapoint datapoint : datapoints) {
            timestamp = datapoint.getTimestamp();
            if (!isFirstStep && (timestamp - priorTimestemp != secondsPerPoint)) {
                Interval interval = intervalFactory.createInterval(factoryParam);
                intervals.add(interval);
                factoryParam.reset(timestamp);
            }
            factoryParam.addValue(datapoint.getValue());
            priorTimestemp = timestamp;
            isFirstStep = false;
        }
        return new Metric(series.getId(), intervals);
    }
}
