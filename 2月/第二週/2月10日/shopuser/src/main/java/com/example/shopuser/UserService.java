package com.example.shopuser;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    // 模擬已註冊的帳號
    private Set<String> registeredEmails = new HashSet<>();
    private boolean loggedIn = false;  // 判斷是否已登入

    // 註冊帳號
    public String register(String email) {
        if (!email.endsWith("@gmail.com")) {
            return "請使用 @gmail.com 網域的 email！";
        }

        String normalizedEmail = email.toLowerCase();  // 將 email 轉為小寫，確保大小寫視為相同

        if (registeredEmails.contains(normalizedEmail)) {
            return "此 email 已經註冊，請使用其他 email 或刪除帳號後重新註冊！";
        } else {
            registeredEmails.add(normalizedEmail);
            return "註冊成功！";
        }
    }

    // 登入帳號
    public String login(String email) {
        if (!email.endsWith("@gmail.com")) {
            return "請使用 @gmail.com 網域的 email！";
        }

        String normalizedEmail = email.toLowerCase();

        if (registeredEmails.contains(normalizedEmail)) {
            loggedIn = true;
            return "登入成功！";
        } else {
            return "帳號未註冊！";
        }
    }

    // 登出帳號
    public String logout() {
        if (loggedIn) {
            loggedIn = false;
            return "已登出！";
        } else {
            return "未登入！";
        }
    }

    // 刪除帳號
    public String deleteAccount(String email) {
        String normalizedEmail = email.toLowerCase();

        if (registeredEmails.contains(normalizedEmail)) {
            registeredEmails.remove(normalizedEmail);
            return "帳號已刪除！";
        } else {
            return "此帳號不存在！";
        }
    }

    // 檢查是否已登入
    public boolean isLoggedIn() {
        return loggedIn;
    }
}
