package com.example.sayhello.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SayHelloController {

    // 使用 @Value 從 application.properties 中讀取 spring.application.name 的值
    @Value("${spring.application.name}")
    private String appName;

    // 訪問 /api/sayhello 時返回包含應用名稱的問候語
    @GetMapping("/sayhello")
    public String sayHello() {
        return "呼叫 " + appName + "!";
    }
}
