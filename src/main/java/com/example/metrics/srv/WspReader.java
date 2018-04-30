package com.example.metrics.srv;

import com.example.metrics.entity.wsp.Series;

import java.io.IOException;
import java.util.List;


public interface WspReader {
    List<Series> getSeriesListBySeriesIds(List<String> seriesIds) throws IOException;

}
