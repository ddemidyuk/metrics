package com.example.metrics.wsp.entities;

import java.util.*;

public class Archive {
    private final ArchiveInfo archiveInfo;
    private final Set<Datapoint> datapoints;

    public Archive(ArchiveInfo archiveInfo) {
        this.archiveInfo = archiveInfo;
        this.datapoints =  new LinkedHashSet<>();
    }

    public Archive(ArchiveInfo archiveInfo, Comparator<Datapoint> datapointComparator) {
        this.archiveInfo = archiveInfo;
        this.datapoints =  new TreeSet<>(datapointComparator);
    }

    public ArchiveInfo getArchiveInfo() {
        return archiveInfo;
    }

    public Set<Datapoint> getDatapoints() {
        return datapoints;
    }

    public void addDatapoint(Datapoint datapoint){
        datapoints.add(datapoint);
    }
}
