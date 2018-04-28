package com.example.metrics.entity;

public class ArchiveInfo {
    private final Long offset;
    private final Long secondsPerPoint;
    private final Long points;

    private ArchiveInfo(Builder builder) {
        this.offset = builder.offset;
        this.secondsPerPoint = builder.secondsPerPoint;
        this.points = builder.points;
    }

    public Long getOffset() {
        return offset;
    }

    public Long getSecondsPerPoint() {
        return secondsPerPoint;
    }

    public Long getPoints() {
        return points;
    }


    public static final class Builder {
        private Long offset;
        private Long secondsPerPoint;
        private Long points;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder offset(Long offset) {
            this.offset = offset;
            return this;
        }

        public Builder secondsPerPoint(Long secondsPerPoint) {
            this.secondsPerPoint = secondsPerPoint;
            return this;
        }

        public Builder points(Long points) {
            this.points = points;
            return this;
        }

        public ArchiveInfo build() {
            return new ArchiveInfo(this);
        }
    }
}
