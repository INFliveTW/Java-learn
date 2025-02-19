package com.example.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // @SpringBootApplication 註解
public class WeatherApiApplication { // WeatherApiApplication 類別

    public static void main(String[] args) {
        SpringApplication.run(WeatherApiApplication.class, args);
    }
}