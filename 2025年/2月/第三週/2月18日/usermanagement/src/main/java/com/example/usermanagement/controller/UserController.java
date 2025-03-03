package com.example.usermanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.usermanagement.model.User;
import com.example.usermanagement.service.UserService;

package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 查詢所有用戶
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 根據 ID 查詢用戶
    @GetMapping("/get")
    public User getUserById(@RequestParam(name = "id") String id) {
        System.out.println("id: " + id);
        return userService.getUserById(Integer.parseInt(id));
    }

    // 新增用戶，支援多值參數 hobbies
    @PostMapping
    public void addUser(@RequestBody User user,
                        @RequestParam(value = "hobbies", required = false) List<String> hobbies) {
        if (hobbies != null && !hobbies.isEmpty()) {
            user.setHobbies(hobbies); // 如果有提供 hobbies，設置到 User 物件中
        }
        userService.addUser(user);
        System.out.println("輸入的名稱: " + user.getName());
        System.out.println("輸入的電子郵件: " + user.getEmail());
        System.out.println("輸入的興趣: " + (user.getHobbies() != null ? String.join(", ", user.getHobbies()) : "無"));
    }

    // 更新用戶，支援路徑參數 id 和多值參數 hobbies
    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") int id,
                           @RequestBody User user,
                           @RequestParam(value = "hobbies", required = false) List<String> hobbies) {
        if (hobbies != null && !hobbies.isEmpty()) {
            user.setHobbies(hobbies); // 更新 hobbies 如果有提供
        }
        user.setId(id); // 確保 ID 一致
        userService.updateUser(id, user);
    }

    // 刪除用戶
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
    }
}
/*
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/get")
    public User getUserById(@RequestParam(name = "id") String id) {
        System.out.println("id: " + id);
        return userService.getUserById(Integer.parseInt(id));
    }

    @PostMapping
    public void addUser(@RequestBody User user) {
    userService.addUser(user);
    System.out.println("輸入的名稱: " + user.getName());
    System.out.println("輸入的電子郵件: " + user.getEmail());
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") int id, @RequestBody User user) {
    userService.updateUser(id, user);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
    }
}
    */