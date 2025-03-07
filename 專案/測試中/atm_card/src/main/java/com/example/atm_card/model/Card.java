package com.example.atm_card.model;

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
@Table(name = "card")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ATM 卡 ID (自動生成)

    @Column(nullable = false, unique = true)
    private String cardNumber;  // 卡片號碼

    @Column(nullable = false)
    private String cardPin;  // 卡片密碼 (四位數密碼，例如 "0000")

    @ManyToOne  // 設定多對一關聯，一張卡對應一個帳戶
    @JoinColumn(name = "account_id", nullable = false)  // 關聯到 Account 表的 ID
    private Account account;  // 對應的帳戶
}
