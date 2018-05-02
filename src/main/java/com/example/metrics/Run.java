package com.example.metrics;

import com.example.metrics.interval.entities.Metric;
import com.example.metrics.interval.srv.IntervalService;
import com.example.metrics.wsp.entities.Datapoint;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Component
public class Run {
    private static final Path csvPath = Paths.get("C:\\TEMP\\metrics.csv");

    @Autowired
    IntervalService intervalService;

    public  void start(String... args) throws IOException {
        String[] seriesIds = {"adserver-00\\cpu\\percent\\wait.wsp"};

        List<Metric> seriesList = intervalService.getMetrics();
        createMetricCsv(csvPath, seriesIds);
        /*saveToCsv(csvPath, seriesList.get(0).getArchives().get(0).getDatapoints());
        createMetricCsv(Paths.get("C:\\TEMP\\metrics1.csv"), seriesIds);
        saveToCsv(Paths.get("C:\\TEMP\\metrics1.csv"), seriesList.get(1).getArchives().get(0).getDatapoints());
        createMetricCsv(Paths.get("C:\\TEMP\\metrics2.csv"), seriesIds);
        saveToCsv(Paths.get("C:\\TEMP\\metrics2.csv"), seriesList.get(2).getArchives().get(0).getDatapoints());*/

    }

    private void saveToCsv(Path csvPath, Set<Datapoint> datapoints) {
        try(PrintWriter csvFile = new PrintWriter(csvPath.toFile())){
            CSVPrinter csvPrinter = new CSVPrinter(csvFile, CSVFormat.DEFAULT);
            for(Datapoint datapoint : datapoints){
                csvPrinter.print(datapoint.getDate());
                csvPrinter.print(datapoint.getValue());
                csvPrinter.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMetricCsv(Path path, String[] metricIds) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(PrintWriter csvFile = new PrintWriter(Files.createFile(path).toFile())){
            CSVPrinter csvPrinter = new CSVPrinter(csvFile, CSVFormat.DEFAULT);
            csvPrinter.print("timestamp");

            for(String metricId : metricIds){
                csvPrinter.print(metricId);
            }

            csvPrinter.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
