package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.StorableInterval;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class SimpleInterval implements StorableInterval {
    private static long nextUid = 0;
    private final long uid;

    private final int startTimestamp;
    private final int endTimestamp;
    private double[] values;
    private final int secondsPerPoint;

    private boolean isStored;
    private Path filePath;
    private int valuesSize;

    private SimpleInterval(Builder builder) {
        uid = nextUid++;
        this.startTimestamp = builder.startTimestamp;
        this.endTimestamp = builder.endTimestamp;
        this.values = builder.values;
        this.secondsPerPoint = builder.secondsPerPoint;
        isStored = false;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    synchronized
    public double[] getValues() {
        if (isStored) restoreValues();
        return values;
    }

    synchronized
    public Double getValue(int timestamp) {
        if (timestamp < startTimestamp || timestamp > getEndTimestamp()) {
            return null;
        }
        if (isStored) restoreValues();
        return values[(timestamp - startTimestamp) / secondsPerPoint];
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public int getEndTimestamp() {
        return endTimestamp;
    }

    synchronized
    public void storeValues(String folderPath) {
        valuesSize = values.length;
        filePath = Paths.get(folderPath, "SimpleInterval_" + uid);
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
        isStored = false;
    }

    public static final class Builder {
        private int startTimestamp;
        private int endTimestamp;
        private double[] values;
        private int secondsPerPoint;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder startTimestamp(int startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public Builder values(double[] values) {
            this.values = values;
            return this;
        }

        public Builder secondsPerPoint(int secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return this;
        }

        public Builder endTimestamp(int endTimestamp) {
            this.endTimestamp = endTimestamp;
            return this;
        }

        public SimpleInterval build() {
            return new SimpleInterval(this);
        }
    }
}
