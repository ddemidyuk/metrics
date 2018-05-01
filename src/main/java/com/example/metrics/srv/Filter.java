package com.example.metrics.srv;

import com.example.metrics.entity.wsp.ArchiveInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Filter {
    private int timestampToAggregateAccuracy;

    public Filter(int timestampToAggregateAccuracy) {
        if (timestampToAggregateAccuracy <= 0) {
            throw new RuntimeException("timestampToAggregateAccuracy must be above zero");
        }
        this.timestampToAggregateAccuracy = timestampToAggregateAccuracy;
    }

    Predicate<ArchiveInfo> getArchiveInfoFilter() {
        return archiveInfo -> archiveInfo.getSecondsPerPoint() > timestampToAggregateAccuracy;
    }

    public List<ArchiveInfo> filterArchiveInfos(List<ArchiveInfo> archiveInfos) {
        List<ArchiveInfo> filteredArchiveInfos = new ArrayList<>();
        for (ArchiveInfo archiveInfo : archiveInfos) {
            if (archiveInfo.getSecondsPerPoint()> timestampToAggregateAccuracy){
                filteredArchiveInfos.add(archiveInfo);
                break; //todo
            }
        }
        return filteredArchiveInfos;
    }
}
