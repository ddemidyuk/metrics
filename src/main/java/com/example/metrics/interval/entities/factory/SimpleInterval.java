package com.example.metrics.interval.entities.factory;

import com.example.metrics.interval.entities.AbstractInterval;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class SimpleInterval extends AbstractInterval {
    private static long nextUid = 0;
    private final long uid;
    private double[] values;

    private boolean isStored;
    private Path filePath;
    private int valuesSize;

    private SimpleInterval(Builder builder) {
        super(builder);
        uid = nextUid++;
        this.values = builder.values;
        isStored = false;
    }

    synchronized
    public double[] getValues() {
        if (isStored) restoreValues();
        return values;
    }

    synchronized
    public Double getValue(int timestamp) {
        if (isContainsTimestamp(timestamp)) {
            if (isStored) restoreValues();
            return values[getTimestampPositionInPeriod(timestamp)];
        }
        return null;
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

    public static final class Builder extends AbstractInterval.AbstractIntervalBuilder<Builder> {
        private double[] values;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder values(double[] values) {
            this.values = values;
            return this;
        }

        public SimpleInterval build() {
            return new SimpleInterval(this);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
