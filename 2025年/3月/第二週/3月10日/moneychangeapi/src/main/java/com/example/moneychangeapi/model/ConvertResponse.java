package com.example.moneychangeapi.model;

import lombok.Data;

@Data
public class ConvertResponse {
    private Query query; //貨幣兌換查詢
    private Conversion conversion;

    @Data
    public static class Query {
        private double amount;
        private String from;
        private String to;
    }

    @Data
    public static class Conversion { //貨幣兌換結果
        private Double result;
    }
}