package com.example.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Run {

    @Autowired
    MainService mainService;

    public void start(String... args) throws IOException {
        mainService.doIt();
        System.out.println("Successfully completed");
    }
}
