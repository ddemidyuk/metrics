package com.example.metrics.srv;

import com.example.metrics.wsp.entities.Series;

import java.io.IOException;
import java.util.List;


public interface SeriesService {
    List<Series> getSeriesListBySeriesIds() throws IOException;
}
