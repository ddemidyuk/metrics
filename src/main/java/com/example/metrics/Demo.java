package com.example.metrics;

import com.example.metrics.entity.ArchiveInfo;
import com.example.metrics.entity.Datapoint;
import com.example.metrics.entity.Metadata;
import com.example.metrics.srv.WspReader;
import com.example.metrics.srv.WspReaderImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;


public class Demo {

    public static final Path wspPath = Paths.get("C:\\TEMP\\adserver-00\\cpu\\percent\\user.wsp");
    public static final Path csvPath = Paths.get("C:\\TEMP\\metrics.csv");
    public static final int HEADER_BLOCK_SIZE = 40;
    public static final int DATA_BLOCK_SIZE = 518400;//1044436;

    public static void main(String[] args) {
        WspReader wspReader = new WspReaderImpl();
        String[] metricIds = {"adserver-00\\cpu\\percent\\wait.wsp"};
        createMetricCsv(csvPath, metricIds);
        Deque<Datapoint> datapoints = getMetrics(wspPath);
        saveToCsv(csvPath, datapoints);

    }

    private static Deque<Datapoint> getMetrics(Path path) {
        Deque<Datapoint> resultDatapoints = new LinkedList<>();
        try (ReadableByteChannel byteChannel = Files.newByteChannel(path)) {


            ByteBuffer headerBuf = ByteBuffer.allocate(HEADER_BLOCK_SIZE);
            byteChannel.read(headerBuf);
            headerBuf.rewind();
            Metadata metadata =Metadata.Builder.newInstance()
                    .aggregationType(getUnsignedLongFrom4Bytes(headerBuf))
                    .maxRetention(getUnsignedLongFrom4Bytes(headerBuf))
                    .xFilesFactor(headerBuf.getFloat())
                    .archiveCount(getUnsignedLongFrom4Bytes(headerBuf))
                    .build();

            ArchiveInfo archiveInfo = ArchiveInfo.Builder.newInstance()
                    .offset(getUnsignedLongFrom4Bytes(headerBuf))
                    .secondsPerPoint(getUnsignedLongFrom4Bytes(headerBuf))
                    .points(getUnsignedLongFrom4Bytes(headerBuf))
                    .build();

            ArchiveInfo archiveInfo2 = ArchiveInfo.Builder.newInstance()
                    .offset(getUnsignedLongFrom4Bytes(headerBuf))
                    .secondsPerPoint(getUnsignedLongFrom4Bytes(headerBuf))
                    .points(getUnsignedLongFrom4Bytes(headerBuf))
                    .build();


            ByteBuffer buf = ByteBuffer.allocate(DATA_BLOCK_SIZE);
            byteChannel.read(buf);
            buf.rewind();

            do {
                int fourBytesIJustRead = buf.getInt();
                if (fourBytesIJustRead == 0) continue;
                long timeInt = fourBytesIJustRead & 0xffffffffl;
                double value = buf.getDouble();
                if (value == 0.0) continue;
                Datapoint datapoint = new Datapoint(new Date(timeInt * 1000), value);
                resultDatapoints.addLast(datapoint);
            } while (buf.hasRemaining());

        } catch (IOException e) {
            System.out.println("I/O Error " + e);
        }

        return resultDatapoints;
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

    private static long getUnsignedLongFrom4Bytes(ByteBuffer buf){
        return ((long)buf.getInt()) & 0xffffffffffffffffl;
    }


}

