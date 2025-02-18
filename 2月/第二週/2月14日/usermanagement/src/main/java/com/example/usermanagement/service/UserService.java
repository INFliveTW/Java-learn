package com.example.usermanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }
    
    public void updateUser(int id, User user) {
        User existingUser = userRepository.findById(id);
        if (existingUser != null) {
            // 只更新有提供的新值
            if (user.getName() != null && !user.getName().isEmpty()) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                existingUser.setEmail(user.getEmail());
            }
            userRepository.update(existingUser);
        }
    }
    
    
    public boolean deleteUser(int id) {
        return userRepository.deleteById(id) > 0;
    }
    
}
