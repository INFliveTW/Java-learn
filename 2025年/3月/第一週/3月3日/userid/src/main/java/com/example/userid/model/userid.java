package com.example.userid.model;

import java.util.List;

public class userid {
    private int id;
    private String name;
    private String email;
    private List<String> hobbies; // 新增 hobbies 欄位

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getHobbies() { return hobbies; }
    public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }
}