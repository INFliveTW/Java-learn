package com.example.usermanagement.repository;

import com.example.usermanagement.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper  // 加上這個註解，讓 MyBatis 能夠正確掃描這個介面
public interface UserRepository {

    @Select("SELECT * FROM users")
    List<User> findAll();

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(int id);

    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    void deleteById(int id);
}
