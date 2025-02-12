package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopuser.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 註冊帳號
    @PostMapping("/register")
    public String register(@RequestParam String email) {
        return userService.register(email);
    }

    // 登入帳號
    @PostMapping("/login")
    public String login(@RequestParam String email) {
        return userService.login(email);
    }

    // 登出帳號
    @PostMapping("/logout")
    public String logout() {
        return userService.logout();
    }

    // 刪除帳號
    @DeleteMapping("/delete")
    public String deleteAccount(@RequestParam String email) {
        return userService.deleteAccount(email);
    }
}
