package com.currencyexchange.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Value;

@Entity
@Value
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String fromCurrency;
    String toCurrency;
    Double rate;
}