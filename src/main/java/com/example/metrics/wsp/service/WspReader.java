package com.example.metrics.wsp.service;

import com.example.metrics.wsp.entities.Series;

import java.nio.file.Path;


public interface WspReader {
    Series getSeriesByWspFilePath(Path path, Filter filter);
}
