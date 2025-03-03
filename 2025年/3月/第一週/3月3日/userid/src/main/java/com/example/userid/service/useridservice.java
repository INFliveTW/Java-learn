package com.example.userid.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.userid.model.userid;
import com.example.userid.repository.useridrepository;

@Service
public class useridservice {
    private final useridrepository useridrepository;

    public useridservice(useridrepository useridrepository) {
        this.useridrepository = useridrepository;
    }

    public List<userid> getAllUsers() {
        return useridrepository.findAll();
    }

    public userid getUserById(int id) {
        return useridrepository.findById(id);
    }

    public void addUser(userid user) {
        useridrepository.save(user);
    }

    public void updateUser(int id, userid user) {
        userid existingUser = useridrepository.findById(id);
        if (existingUser != null) {
            if (user.getName() != null && !user.getName().isEmpty()) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getHobbies() != null) {
                existingUser.setHobbies(user.getHobbies());
            }
            useridrepository.update(existingUser);
        }
    }

    public boolean deleteUser(int id) {
        return useridrepository.deleteById(id) > 0;
    }
}