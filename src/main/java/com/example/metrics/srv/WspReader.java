package com.example.metrics.srv;

import com.example.metrics.entity.wsp.Series;

import java.nio.file.Path;


public interface WspReader {
    Series getSeriesByWspFilePath(Path path, Filter filter);
}
