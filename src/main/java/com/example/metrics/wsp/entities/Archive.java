package com.example.metrics.wsp.entities;

import java.util.*;

public class Archive {
    private final ArchiveInfo archiveInfo;
    private final Set<Datapoint> datapoints;
    private int zeroValueCount = 0;

    public Archive(ArchiveInfo archiveInfo) {
        this.archiveInfo = archiveInfo;
        this.datapoints = new LinkedHashSet<>();
    }

    public Archive(ArchiveInfo archiveInfo, Comparator<Datapoint> datapointComparator) {
        this.archiveInfo = archiveInfo;
        this.datapoints = new TreeSet<>(datapointComparator);
    }

    public ArchiveInfo getArchiveInfo() {
        return archiveInfo;
    }

    public Set<Datapoint> getDatapoints() {
        return datapoints;
    }

    public double getZeroRatio() {
        return zeroValueCount / datapoints.size();
    }

    public void addDatapoint(Datapoint datapoint) {
        if (datapoint.getValue() == 0.0) {
            zeroValueCount++;
        }
        datapoints.add(datapoint);
    }
}
