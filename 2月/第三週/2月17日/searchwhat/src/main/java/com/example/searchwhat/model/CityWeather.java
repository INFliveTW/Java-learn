package com.example.searchwhat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "city_weather")  // 明確指定對應的資料庫表格名稱
public class CityWeather {

    @Id  // 設定為主鍵
    private String cityName;  // 城市名稱，這裡是主鍵

    private String temperature;  // 城市天氣的氣溫
    private String condition;    // 城市天氣的狀況（例如：晴天、陰天等）
}
