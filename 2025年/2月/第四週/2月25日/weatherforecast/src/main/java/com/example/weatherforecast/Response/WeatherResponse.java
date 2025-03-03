package com.example.weatherforecast.Response;

import lombok.Data;

@Data
public class WeatherResponse {
    private String city;
    private String forecast;
}