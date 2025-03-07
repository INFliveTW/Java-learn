package com.example.atm_card.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.atm_card.exception.ResourceNotFoundException;
import com.example.atm_card.model.Account;
import com.example.atm_card.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service  // 標記為 Spring 服務層
@RequiredArgsConstructor  // 自動注入 Repository
public class AccountService {

    private final AccountRepository accountRepository;  // 帳戶資料庫存取層

    /**
     * 更新帳戶餘額（存款或提款）
     * @param accountNumber 帳戶號碼
     * @param amount 變更的金額
     * @return 更新後的帳戶資訊
     */
    @Transactional
    public Account updateBalance(String accountNumber, Double amount) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(account -> {
                    account.setBalance(account.getBalance() + amount);  // 更新餘額
                    return accountRepository.save(account);  // 儲存更新後的帳戶
                })
                .orElseThrow(() -> new ResourceNotFoundException("帳戶不存在或更新失敗：" + accountNumber));
    }
}
