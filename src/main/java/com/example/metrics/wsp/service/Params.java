package com.example.metrics.wsp.service;

public class Params {
    private final String rootPath;
    private final String seriesId;
    private final Filter filter;

    public Params(Builder builder) {
        this.rootPath = builder.rootPath;
        this.seriesId = builder.seriesId;
        this.filter = builder.filter;
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public Filter getFilter() {
        return filter;
    }

    public static final class Builder {
        private String rootPath;
        private String seriesId;
        private Filter filter;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder rootPath(String rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public Builder seriesId(String seriesId) {
            this.seriesId = seriesId;
            return this;
        }

        public Builder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public Params build() {
            return new Params(this);
        }
    }
}
