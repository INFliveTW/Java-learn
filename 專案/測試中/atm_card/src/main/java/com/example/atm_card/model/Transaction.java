package com.example.atm_card.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 交易 ID (自動生成)

    @ManyToOne  // 設定多對一關聯，一個帳戶可有多筆交易
    @JoinColumn(name = "account_id", nullable = false)  // 關聯到 Account 表的 ID
    private Account account;  // 交易的帳戶

    @Column(nullable = false)
    private Double amount;  // 交易金額 (正數代表存款，負數代表提款)

    @Column(nullable = false)
    private String transactionType;  // 交易類型 (Deposit 存款 / Withdraw 提款)

    @Column(nullable = false)
    private LocalDateTime transactionTime;  // 交易時間
}
