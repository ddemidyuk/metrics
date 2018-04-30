package com.example.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MetricsApplication implements CommandLineRunner {

    @Autowired
    Run run;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MetricsApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        run.start(args);
    }
}
