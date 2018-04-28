package com.example.metrics.srv;

import com.example.metrics.entity.wsp.Series;

public interface WspReader {
    Series getSeriesByWspFilePath(String seriesId);

}
