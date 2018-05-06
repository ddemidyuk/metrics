package com.example.metrics;

import com.example.metrics.interval.entities.Interval;
import com.example.metrics.interval.entities.Metric;
import com.example.metrics.interval.entities.Metrics;
import com.example.metrics.interval.entities.factory.IntervalFactory;
import com.example.metrics.interval.entities.factory.IntervalFactoryParam;
import com.example.metrics.wsp.entities.Archive;
import com.example.metrics.wsp.entities.Datapoint;
import com.example.metrics.wsp.entities.Series;
import com.example.metrics.wsp.service.Filter;
import com.example.metrics.wsp.service.Params;
import com.example.metrics.wsp.service.WspReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MainServiceImpl implements MainService {

    private WspReader wspReader;

    private AppProperties appProperties;

    private IntervalFactory intervalFactory;

    @Autowired
    public MainServiceImpl(WspReader wspReader, AppProperties appProperties, IntervalFactory intervalFactory) {
        this.wspReader = wspReader;
        this.appProperties = appProperties;
        this.intervalFactory = intervalFactory;
    }

    public void doIt() {
        Path csvPath = Paths.get(appProperties.getOutputCsvPath(), "AggrMetrics.csv");
        createMetricCsv(csvPath);
        Metrics metrics = getMetrics();
        List<Double> values = new ArrayList<>(metrics.get().size());
        /*for (int timestamp = metrics.getMinStartTimestamp(); timestamp <= metrics.getMaxEndTimestamp(); timestamp += appProperties.getSecondsPerPoint()) {
            for (Metric metric : metrics) {
                values.add(metric.getValue(timestamp));
            }
            saveToCsv(csvPath,timestamp, values);
        }*/
    }

    private void saveToCsv(Path csvPath, int timestamp, List<Double> values) {
        try (PrintWriter csvFile = new PrintWriter(csvPath.toFile())) {
            CSVPrinter csvPrinter = new CSVPrinter(csvFile, CSVFormat.DEFAULT);
            csvPrinter.print(new SimpleDateFormat("dd.MM.yyyy hh:mm").format(new Date(timestamp * 1000L)));
            for (Double value : values) {
                csvPrinter.print(value);
            }
            csvPrinter.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMetricCsv(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter csvFile = new PrintWriter(Files.createFile(path).toFile())) {
            CSVPrinter csvPrinter = new CSVPrinter(csvFile, CSVFormat.DEFAULT);
            csvPrinter.print("timestamp");

         /*   for (String metricId : metricIds) {
                csvPrinter.print(metricId);
            }*/

            csvPrinter.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Metrics getMetrics() {
        Filter filter = Filter.Builder.newInstance()
                .timestampPredicate(Filter.SKIP_ZERO_INT_PREDICATE)
                .secondsPerPointPredicate(s -> s == appProperties.getSecondsPerPoint())
                .datapointComparator(Filter.ASC_DATAPOINT_COMPARATOR)
                .build();

        Metrics metrics = new Metrics();

        appProperties.getMetricIds().stream()
                .map(metricId -> Params.Builder.newInstance()
                        .seriesId(metricId)
                        .rootPath(appProperties.getInputDataRootPath())
                        .filter(filter)
                        .build())
                .map(wspReader::getSeriesByWspFilePath)
                .map(this::getMetricFromFirstArchiveOfSeries)
                .forEach(metric -> metrics.addMetric(metric));

        return metrics;
    }

    public List<Series> getSeries() {
        Filter filter = Filter.Builder.newInstance()
                .timestampPredicate(Filter.SKIP_ZERO_INT_PREDICATE)
                .secondsPerPointPredicate(s -> s == appProperties.getSecondsPerPoint())
                //.datapointComparator(Filter.ASC_DATAPOINT_COMPARATOR)
                .build();

        return appProperties.getMetricIds().stream()
                .map(metricId -> Params.Builder.newInstance()
                        .seriesId(metricId)
                        .rootPath(appProperties.getInputDataRootPath())
                        .filter(filter)
                        .build())
                .map(wspReader::getSeriesByWspFilePath)
                .collect(Collectors.toList());
    }

    //todo переписать говнокод
    private Metric getMetricFromFirstArchiveOfSeries(Series series) {
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