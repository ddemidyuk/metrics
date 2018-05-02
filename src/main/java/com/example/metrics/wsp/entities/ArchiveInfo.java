package com.example.metrics.wsp.entities;

public class ArchiveInfo {
    private final int offset;
    private final int secondsPerPoint;
    private final int points;

    private ArchiveInfo(Builder builder) {
        this.offset = builder.offset;
        this.secondsPerPoint = builder.secondsPerPoint;
        this.points = builder.points;
    }

    public int getOffset() {
        return offset;
    }

    public int getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public int getPoints() {
        return points;
    }


    public static final class Builder {
        private int offset;
        private int secondsPerPoint;
        private int points;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder offset(int offset) {
            this.offset = offset;
            return this;
        }

        public Builder secondsPerPoint(int secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return this;
        }

        public Builder points(int points) {
            this.points = points;
            return this;
        }

        public ArchiveInfo build() {
            return new ArchiveInfo(this);
        }
    }
}
