//天氣數據模型
package com.example.weather.model;

import lombok.Data;

@Data
public class WeatherResponse {
    private Main main;
    private String name;
    private Weather[] weather;

    @Data
    public static class Main {
        private double temp;
        private int humidity;
    }

    @Data
    public static class Weather {
        private String description;
    }
}