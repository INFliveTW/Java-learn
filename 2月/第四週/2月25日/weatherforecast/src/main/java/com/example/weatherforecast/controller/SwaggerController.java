package com.example.weatherforecast.controller;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.weatherforecast.Response.WeatherResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/weather")
@Tag(name = "天氣預報 API", description = "提供天氣預報相關的 API 端點")
public class SwaggerController {

    @Value("${weather.api-key}")
    private String apiKey;

    @Value("${weather.base-url}")
    private String baseUrl;

    @GetMapping("/forecast")
    @Operation(summary = "獲取 5 天天氣預報", description = "輸入城市名稱，返回未來 5 天天氣預報")
    public WeatherResponse getWeatherForecast(
            @Parameter(description = "城市名稱", required = true) @RequestParam("city") String city) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = baseUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric&cnt=5";
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                WeatherResponse weather = new WeatherResponse();
                weather.setCity(city);
                weather.setForecast(jsonResponse);
                return weather;
            }
        } catch (Exception e) {
            WeatherResponse errorResponse = new WeatherResponse();
            errorResponse.setCity(city);
            errorResponse.setForecast("Error: " + e.getMessage());
            return errorResponse;
        }
    }
}