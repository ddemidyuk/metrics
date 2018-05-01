package com.example.metrics.entity.wsp;

import java.util.List;

public class Header {
    private final Metadata metadata;
    private List<ArchiveInfo> archiveInfos;

    public Header(Metadata metadata, List<ArchiveInfo> archiveInfos) {
        this.metadata = metadata;
        this.archiveInfos = archiveInfos;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public List<ArchiveInfo> getArchiveInfos() {
        return archiveInfos;
    }

    public void setArchiveInfos(List<ArchiveInfo> archiveInfos) {
        this.archiveInfos = archiveInfos;
    }
}
