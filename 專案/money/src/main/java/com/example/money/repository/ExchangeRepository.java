package com.example.money.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.money.model.ExchangeRateEntity;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeRateEntity, Long> {
    // 根據 fromCurrency 和 toCurrency 查詢匯率資料
    Optional<ExchangeRateEntity> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}
