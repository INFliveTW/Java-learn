package com.example.searchwhat.controller;

import com.example.searchwhat.model.CityWeather;
import com.example.searchwhat.service.CityWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private CityWeatherService cityWeatherService;

    // 查詢城市天氣
    @GetMapping("/city/{cityName}")
    public CityWeather getWeather(@PathVariable String cityName) {
        return cityWeatherService.getWeatherByCity(cityName);
    }

    // 新增城市天氣
    @PostMapping("/city")
    public CityWeather addCityWeather(@RequestBody CityWeather cityWeather) {
        return cityWeatherService.addCityWeather(cityWeather);
    }

    // 更新城市天氣
    @PutMapping("/city")
    public CityWeather updateCityWeather(@RequestBody CityWeather cityWeather) {
        return cityWeatherService.updateCityWeather(cityWeather);
    }

    // 刪除城市天氣
    @DeleteMapping("/city/{cityName}")
    public void deleteCityWeather(@PathVariable String cityName) {
        cityWeatherService.deleteCityWeather(cityName);
    }
}
