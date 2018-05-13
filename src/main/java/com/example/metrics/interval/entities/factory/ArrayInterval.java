package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.AbstractStorableInterval;
import com.example.metrics.interval.entities.Period;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.BiFunction;

public class ArrayInterval extends AbstractStorableInterval {
    public static final String STORAGE_FILE_PREFIX = "ArrayInterval_";
    private static long nextUid = 0;
    private final long uid;
    private double[] values;

    private boolean isStored;
    private Path filePath;
    private int valuesSize;

    private ArrayInterval(Builder builder) {
        super(builder);
        uid = nextUid++;
        this.values = builder.values;
        isStored = false;
    }

    synchronized
    public double[] getValues() {
        while (isStored) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    synchronized
    public Double getValue(int timestamp) {
        if (isContainsTimestamp(timestamp)) {
            while (isStored) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return values[getTimestampPositionInPeriod(timestamp)];
        }
        return null;
    }

    synchronized
    public void storeValues(String folderPath) {
        valuesSize = values.length;
        filePath = Paths.get(folderPath, STORAGE_FILE_PREFIX + uid);
        try (WritableByteChannel byteChannel = Files.newByteChannel(filePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer buf = ByteBuffer.allocate(valuesSize * Double.BYTES);
            buf.asDoubleBuffer().put(values);
            buf.rewind();
            byteChannel.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        values = null;
        isStored = true;
    }

    synchronized
    public void restoreValues() {
        if(!isStored){
            return;
        }
        if (Files.exists(filePath)) {
            restoreValuesFromTmpFolder();
        } else {
            restoreValuesFromDb();
        }
        isStored = false;
        notifyAll();
    }

    synchronized
    private void restoreValuesFromTmpFolder() {
        try (ReadableByteChannel byteChannel = Files.newByteChannel(filePath)) {
            ByteBuffer buf = ByteBuffer.allocate(valuesSize * Double.BYTES);
            byteChannel.read(buf);
            buf.rewind();
            values = new double[buf.remaining() / Double.BYTES];
            buf.asDoubleBuffer().get(values);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    synchronized
    private void restoreValuesFromDb() {
        values = functionForRestoreFromDb.apply(metricId, getPeriod());
    }


    public static final class Builder extends AbstractStorableInterval.AbstractIntervalBuilder<Builder> {
        private double[] values;
        private String metricId;
        private BiFunction<String, Period, double[]> functionForRestoreFromDb;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder values(double[] values) {
            this.values = values;
            return this;
        }

        public Builder metricId(String metricId) {
            this.metricId = metricId;
            return this;
        }

        public Builder functionForRestoreFromDb(BiFunction<String, Period, double[]> functionForRestoreFromDb) {
            this.functionForRestoreFromDb = functionForRestoreFromDb;
            return this;
        }

        public ArrayInterval build() {
            return new ArrayInterval(this);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
