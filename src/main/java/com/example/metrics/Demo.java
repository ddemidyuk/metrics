package com.example.metrics;

import com.example.metrics.entity.wsp.Datapoint;
import com.example.metrics.entity.wsp.Series;
import com.example.metrics.srv.WspReader;
import com.example.metrics.srv.WspReaderImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;


public class Demo {


    public static final Path csvPath = Paths.get("C:\\TEMP\\metrics.csv");


    public static void main(String[] args) {
        WspReader wspReader = new WspReaderImpl();
        String[] seriesIds = {"adserver-00\\cpu\\percent\\wait.wsp"};
        createMetricCsv(csvPath, seriesIds);
        Series series = wspReader.getSeriesByWspFilePath(seriesIds[0]);
        saveToCsv(csvPath, series.getArchives().get(0).getDatapoints());

    }

    private static void saveToCsv(Path csvPath, Deque<Datapoint> datapoints) {
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

    private static void createMetricCsv(Path path, String[] metricIds) {
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

