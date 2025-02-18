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
