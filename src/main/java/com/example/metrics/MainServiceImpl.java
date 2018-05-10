package com.example.metrics;

import com.example.metrics.interval.entities.Interval;
import com.example.metrics.interval.entities.Metric;
import com.example.metrics.interval.entities.Metrics;
import com.example.metrics.interval.entities.Period;
import com.example.metrics.interval.entities.factory.IntervalFactory;
import com.example.metrics.interval.entities.factory.IntervalFactoryParam;
import com.example.metrics.interval.entities.factory.UsualIntervalFactoryParam;
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
import java.util.*;
import java.util.stream.Stream;

@Service
public class MainServiceImpl implements MainService {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm");
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
        Path csvPath = Paths.get(appProperties.getOutputCsvPath());
        createMetricCsv(csvPath);
        Metrics metrics = getMetrics();
        List<Double> values = new ArrayList<>(metrics.get().size());
        try (PrintWriter csvFile = new PrintWriter(csvPath.toFile())) {
            CSVPrinter csvPrinter = new CSVPrinter(csvFile, CSVFormat.DEFAULT);
            csvPrinter.print("timestamp");
            csvPrinter.printRecord(appProperties.getMetricIds());
            for (int timestamp : metrics.getPeriods()) {
                for (Metric metric : metrics) {
                    values.add(metric.getValue(timestamp));
                }
                saveToCsv(csvPrinter, timestamp, values);
                values.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToCsv(CSVPrinter csvPrinter, int timestamp, List<Double> values) throws IOException {
        csvPrinter.print(DATE_FORMAT.format(new Date(timestamp * 1000L)));
        for (Double value : values) {
            csvPrinter.print(value);
        }
        csvPrinter.println();
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

        Metrics metrics = new Metrics();

        getSeriesByByMetricsIds(appProperties.getMetricIds(), getFilter())
                .map(this::getMetricFromFirstArchiveOfSeries)
                .forEach(metric -> metrics.addMetric(metric));

        return metrics;
    }

    private Filter getFilter() {
        return Filter.Builder.newInstance()
                .timestampPredicate(Filter.SKIP_ZERO_INT_PREDICATE)
                .secondsPerPointPredicate(s -> s == appProperties.getSecondsPerPoint())
                .datapointComparator(Filter.ASC_DATAPOINT_COMPARATOR)
                .build();
    }

    private Stream<Series> getSeriesByByMetricsIds(List<String> metricIds, Filter filter) {
        return metricIds.stream()
                .map(metricId -> getParamsByMetricsId(metricId, filter))
                .map(wspReader::getSeriesByWspFilePath);
    }

    private Params getParamsByMetricsId(String metricId, Filter filter){
        return Params.Builder.newInstance()
                .seriesId(metricId)
                .rootPath(appProperties.getInputDataRootPath())
                .filter(filter)
                .build();
    }

    private double[] getValueByPeriod(String metricId, Period period) {
        Filter filter = getFilter();
        filter.setTimestampPredicate(
                filter.getTimestampPredicate()
                .and(i -> i >= period.getStartTimestamp())
                .and(i -> i <= period.getEndTimestamp())
        );

        Params params = getParamsByMetricsId(metricId, filter);
        Series series = wspReader.getSeriesByWspFilePath(params);
        Archive archive = series.getArchives().get(0);
        Set<Datapoint> datapoints = archive.getDatapoints();
        double[] values = new double[datapoints.size()];
        int i = 0;
        for(Datapoint datapoint : datapoints){
            values[i++]=datapoint.getValue();
        }
        return values;
    }

    //todo refactor this
    private Metric getMetricFromFirstArchiveOfSeries(Series series) {
        Archive archive = series.getArchives().get(0);
        int secondsPerPoint = archive.getArchiveInfo().getSecondsPerPoint();
        Set<Datapoint> datapoints = archive.getDatapoints();

        List<Interval> intervals = new ArrayList<>();
        IntervalFactoryParam factoryParam = UsualIntervalFactoryParam.Builder.newInstance()
                .metricId(series.getId())
                .startTimestamp(datapoints.iterator().next().getTimestamp())
                .secondsPerPoint(secondsPerPoint)
                .bufferSize(datapoints.size())
                .functionForRestoreFromDb(this::getValueByPeriod)
                .build();

        int priorTimestamp = 0;//todo
        int timestamp;

        boolean isFirstStep = true; //todo
        for (Datapoint datapoint : datapoints) {
            timestamp = datapoint.getTimestamp();
            if (!isFirstStep && (timestamp - priorTimestamp != secondsPerPoint)) {
                Interval newInterval = intervalFactory.createInterval(factoryParam);
                intervals.add(newInterval);
                factoryParam.reset(timestamp);
            }
            factoryParam.addValue(datapoint.getValue());
            priorTimestamp = timestamp;
            isFirstStep = false;
        }
        //todo remove it
        if (factoryParam.getValues().length > 0) {
            Interval newInterval = intervalFactory.createInterval(factoryParam);
            intervals.add(newInterval);
        }
        return new Metric(series.getId(), intervals);
    }
}
