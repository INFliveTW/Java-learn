package com.example.atm_card.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.atm_card.exception.ResourceNotFoundException;
import com.example.atm_card.model.Account;
import com.example.atm_card.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Api(tags = "Account Controller", description = "提供帳戶相關的操作，如更新餘額等")
public class AccountController {

    private final AccountService accountService;  // 注入服務層

    /**
     * 更新帳戶餘額（存款或提款）
     * @param accountNumber 帳戶號碼
     * @param amount 存入或提取的金額
     * @return 更新後的帳戶資訊
     */
    @PutMapping("/update-balance")
    @ApiOperation(value = "更新帳戶餘額", notes = "根據帳戶號碼和金額更新帳戶餘額。金額可以是正數（存款）或負數（提款）")
    public ResponseEntity<?> updateBalance(
            @ApiParam(value = "帳戶號碼", required = true) @RequestParam String accountNumber,
            @ApiParam(value = "存入或提取的金額", required = true) @RequestParam Double amount) {

        // 檢查 amount 是否為 null
        if (amount == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("金額不能為空！");
        }

        try {
            Account updatedAccount = accountService.updateBalance(accountNumber, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
