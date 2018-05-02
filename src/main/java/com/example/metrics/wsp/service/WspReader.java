package com.example.metrics.wsp.service;

import com.example.metrics.wsp.entities.Series;


public interface WspReader {
    Series getSeriesByWspFilePath(Params params);
}
