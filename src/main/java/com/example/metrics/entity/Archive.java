package com.example.metrics.entity;

import java.util.Deque;

public class Archive {
    private final ArchiveInfo archiveInfo;
    private final Deque<Datapoint> datapoints;

    public Archive(ArchiveInfo archiveInfo, Deque<Datapoint> datapoints) {
        this.archiveInfo = archiveInfo;
        this.datapoints = datapoints;
    }

    public ArchiveInfo getArchiveInfo() {
        return archiveInfo;
    }

    public Deque<Datapoint> getDatapoints() {
        return datapoints;
    }
}
