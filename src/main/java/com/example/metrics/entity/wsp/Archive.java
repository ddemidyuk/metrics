package com.example.metrics.entity.wsp;

import java.util.TreeSet;

public class Archive {
    private final ArchiveInfo archiveInfo;
    private final TreeSet<Datapoint> datapoints = new TreeSet<>();
    public Archive(ArchiveInfo archiveInfo) {
        this.archiveInfo = archiveInfo;
    }

    public ArchiveInfo getArchiveInfo() {
        return archiveInfo;
    }

    public TreeSet<Datapoint> getDatapoints() {
        return datapoints;
    }

    public void addDatapoint(Datapoint datapoint){
        datapoints.add(datapoint);
    }
}
