package com.example.metrics.csv;

import com.example.metrics.AppProperties;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class CsvWriteQueue {
    private BlockingQueue<List<Object>> queue = new LinkedBlockingQueue<>();
    private Thread writeThread;
    private Path csvPath;
    private AppProperties appProperties;
    private volatile int countOfWriteRecords;

    private Runnable writeTask = () -> {
        createMetricCsv(csvPath);
        try (PrintWriter csvFile = new PrintWriter(csvPath.toFile())) {
            CSVPrinter csvPrinter = new CSVPrinter(csvFile, CSVFormat.DEFAULT);
            while (true) {
                csvPrinter.printRecord(queue.take());
                countOfWriteRecords++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    @Autowired
    public CsvWriteQueue(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.csvPath = Paths.get(appProperties.getOutputCsvPath());
        createMetricCsv(this.csvPath);
        writeThread = new Thread(writeTask);
        writeThread.setDaemon(true);
        writeThread.start();
    }

    public void offer(List<Object> objects) {
        queue.offer(objects);
    }

    private void createMetricCsv(Path path) {
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCountOfWriteRecords() {
        return countOfWriteRecords;
    }

    public boolean queueIsNotEmpty(){
        return !queue.isEmpty();
    }
}
