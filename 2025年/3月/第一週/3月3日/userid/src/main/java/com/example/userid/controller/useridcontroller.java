package com.example.userid.controller;

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

import com.example.userid.model.userid;
import com.example.userid.service.useridservice;

@RestController
@RequestMapping("/userid")
public class useridcontroller {
    private final useridservice useridservice;

    public useridcontroller(useridservice useridservice) {
        this.useridservice = useridservice;
    }

    @GetMapping
    public List<userid> getAllUsers() {
        return useridservice.getAllUsers();
    }

    @GetMapping("/get")
    public userid getUserById(@RequestParam(name = "id") String id) {
        System.out.println("id: " + id);
        return useridservice.getUserById(Integer.parseInt(id));
    }

    @PostMapping
    public void addUser(@RequestBody userid user,
                        @RequestParam(value = "hobbies", required = false) List<String> hobbies) {
        if (hobbies != null && !hobbies.isEmpty()) {
            user.setHobbies(hobbies);
        }
        useridservice.addUser(user);
        System.out.println("輸入的名稱: " + user.getName());
        System.out.println("輸入的電子郵件: " + user.getEmail());
        System.out.println("輸入的興趣: " + (user.getHobbies() != null ? String.join(", ", user.getHobbies()) : "無"));
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") int id,
                           @RequestBody userid user,
                           @RequestParam(value = "hobbies", required = false) List<String> hobbies) {
        if (hobbies != null && !hobbies.isEmpty()) {
            user.setHobbies(hobbies);
        }
        user.setId(id);
        useridservice.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        useridservice.deleteUser(id);
    }
}