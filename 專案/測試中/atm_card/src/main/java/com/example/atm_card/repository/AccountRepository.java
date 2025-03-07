package com.example.atm_card.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.atm_card.model.Account;

@Repository  // 標記這是一個 Spring 管理的 Repository 類別
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * 透過帳戶號碼查詢帳戶資訊
     * @param accountNumber 帳戶號碼
     * @return 帳戶資訊（如果存在）
     */
    Optional<Account> findByAccountNumber(String accountNumber);
}
