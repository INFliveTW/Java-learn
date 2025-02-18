package com.example.usermanagement.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.usermanagement.model.User;

@Mapper  // 加上這個註解，讓 MyBatis 能夠正確掃描這個介面
public interface UserRepository {

    @Select("SELECT * FROM users")
    List<User> findAll();

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(int id);

    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(User user);

    @Update("<script>"
    + "UPDATE users "
    + "<set>"
    + "<if test='name != null and name != \"\"'> name = #{name}, </if>"
    + "<if test='email != null and email != \"\"'> email = #{email}, </if>"
    + "</set>"
    + "WHERE id = #{id}"
    + "</script>")
    void update(User user);


    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(int id);
    

}
