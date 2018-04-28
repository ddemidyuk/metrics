package com.example.metrics.srv;

import com.example.metrics.entity.Series;

public interface WspReader {
    Series getSeriesByWspFilePath(String seriesId);

}
