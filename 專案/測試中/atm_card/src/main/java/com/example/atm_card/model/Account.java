package com.example.atm_card.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity  // 標記這是一個 JPA 實體類別，對應到資料庫中的「帳戶」表
@Table(name = "account")  // 指定資料表名稱為 account
@NoArgsConstructor  // 自動生成無參數建構子
@AllArgsConstructor // 自動生成有參數建構子
@Data  // 自動生成 getter、setter、toString、equals、hashCode 方法
public class Account {

    @Id  // 設定主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 設定自動遞增的主鍵
    private Long id;  // 帳戶 ID (自動生成)

    @Column(nullable = false, unique = true)  // 不能為空且唯一
    private String accountNumber;  // 帳戶號碼

    @Column(nullable = false)  // 不能為空
    private String accountPassword;  // 帳戶密碼 (用來登入)

    @Column(nullable = false)  // 不能為空
    private Double balance;  // 帳戶餘額
}
