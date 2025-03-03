package com.example.searchwhat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.searchwhat.model.CityWeather;

public interface CityWeatherRepository extends JpaRepository<CityWeather, String> {
    // JpaRepository 提供了基本的 CRUD 操作，無需額外實現
}
