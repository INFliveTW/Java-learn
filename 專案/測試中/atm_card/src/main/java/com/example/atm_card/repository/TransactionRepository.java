package com.example.atm_card.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.atm_card.model.Account;
import com.example.atm_card.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 透過帳戶查詢該帳戶的所有交易紀錄
     * @param account 帳戶資訊
     * @return 該帳戶的交易紀錄列表
     */
    List<Transaction> findByAccount(Account account);
}
