package com.example.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Run {

    @Autowired
    MainService mainService;

    public void start(String... args) throws IOException {
        mainService.doIt();

       // List<Metric> seriesList = intervalService.get();
        /*List<Series> seriesList = intervalService.getSeries();
        createMetricCsv(csvPath, seriesIds);
        saveToCsv(csvPath, seriesList.get(0).getArchives().get(0).getDatapoints());
        createMetricCsv(Paths.get("C:\\TEMP\\metrics1.csv"), seriesIds);
        saveToCsv(Paths.get("C:\\TEMP\\metrics1.csv"), seriesList.get(1).getArchives().get(0).getDatapoints());
        createMetricCsv(Paths.get("C:\\TEMP\\metrics2.csv"), seriesIds);
        saveToCsv(Paths.get("C:\\TEMP\\metrics2.csv"), seriesList.get(2).getArchives().get(0).getDatapoints());*/

    }
}
