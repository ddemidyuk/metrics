package com.example.metrics.wsp.entities;

import java.util.List;

public class Series {
    private final String id;
    private final Header header;
    private final List<Archive> archives;


    public Series(String id, Header header, List<Archive> archives) {
        this.id = id;
        this.header = header;
        this.archives = archives;
    }

    public String getId() {
        return id;
    }

    public List<Archive> getArchives() {
        return archives;
    }

    public Header getHeader() {
        return header;
    }
}
