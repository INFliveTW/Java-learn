package com.example.atm_card.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.atm_card.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * 透過卡片號碼查詢卡片資訊
     * @param cardNumber 卡片號碼
     * @return 卡片資訊（如果存在）
     */
    Optional<Card> findByCardNumber(String cardNumber);
}
