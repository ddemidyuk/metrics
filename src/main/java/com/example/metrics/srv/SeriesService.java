package com.example.metrics.srv;

import com.example.metrics.entity.wsp.Series;

import java.io.IOException;
import java.util.List;


public interface SeriesService {
    List<Series> getSeriesListBySeriesIds() throws IOException;
}
