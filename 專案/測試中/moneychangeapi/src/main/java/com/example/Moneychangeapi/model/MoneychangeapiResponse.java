package com.example.Moneychangeapi.model;

import java.util.Map;

import lombok.Data;

@Data
public class MoneychangeapiResponse {
    private String base;
    private Map<String, Double> rates; // 修正為與 API 一致的字段名稱
}