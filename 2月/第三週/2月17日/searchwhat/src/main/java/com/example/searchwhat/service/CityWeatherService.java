package com.example.searchwhat.service;

import com.example.searchwhat.model.CityWeather;
import com.example.searchwhat.repository.CityWeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityWeatherService {

    @Autowired
    private CityWeatherRepository cityWeatherRepository;

    // 查詢城市天氣
    public CityWeather getWeatherByCity(String cityName) {
        return cityWeatherRepository.findById(cityName).orElse(null);
    }

    // 新增城市天氣
    public CityWeather addCityWeather(CityWeather cityWeather) {
        return cityWeatherRepository.save(cityWeather);
    }

    // 更新城市天氣
    public CityWeather updateCityWeather(CityWeather cityWeather) {
        return cityWeatherRepository.save(cityWeather);
    }

    // 刪除城市天氣
    public void deleteCityWeather(String cityName) {
        cityWeatherRepository.deleteById(cityName);
    }
}
