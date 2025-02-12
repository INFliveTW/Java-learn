package com.example.weather.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.weather.entity.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    List<Weather> findByCity(String city);
}
