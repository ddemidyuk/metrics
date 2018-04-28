package com.example.metrics.entity;

public class Metadata {

    private final long aggregationType;
    private final long maxRetention;
    private final double xFilesFactor;
    private final int archiveCount;

    private Metadata(Builder builder) {
        this.aggregationType = builder.aggregationType;
        this.maxRetention = builder.maxRetention;
        this.xFilesFactor = builder.xFilesFactor;
        this.archiveCount = builder.archiveCount;
    }

    public long getAggregationType() {
        return aggregationType;
    }

    public long getMaxRetention() {
        return maxRetention;
    }

    public double getXFilesFactor() {
        return xFilesFactor;
    }

    public int getArchiveCount() {
        return archiveCount;
    }


    public static final class Builder {
        private long aggregationType;
        private long maxRetention;
        private double xFilesFactor;
        private int archiveCount;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder aggregationType(long aggregationType) {
            this.aggregationType = aggregationType;
            return this;
        }

        public Builder maxRetention(long maxRetention) {
            this.maxRetention = maxRetention;
            return this;
        }

        public Builder xFilesFactor(double xFilesFactor) {
            this.xFilesFactor = xFilesFactor;
            return this;
        }

        public Builder archiveCount(int archiveCount) {
            this.archiveCount = archiveCount;
            return this;
        }

        public Metadata build() {
            return new Metadata(this);
        }
    }
}
