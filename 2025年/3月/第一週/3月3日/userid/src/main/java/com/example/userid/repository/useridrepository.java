package com.example.userid.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.userid.model.userid;

@Mapper
public interface useridrepository {

    @Select("SELECT * FROM users")
    List<userid> findAll();

    @Select("SELECT * FROM users WHERE id = #{id}")
    userid findById(int id);

    @Insert("INSERT INTO users(name, email, hobbies) VALUES(#{name}, #{email}, #{hobbies, jdbcType=VARCHAR, typeHandler=com.example.userid.typehandler.ListStringTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(userid user);

    @Update("<script>"
        + "UPDATE users "
        + "<set>"
        + "<if test='name != null and name != \"\"'> name = #{name}, </if>"
        + "<if test='email != null and email != \"\"'> email = #{email}, </if>"
        + "<if test='hobbies != null'> hobbies = #{hobbies, jdbcType=VARCHAR, typeHandler=com.example.userid.typehandler.ListStringTypeHandler}, </if>"
        + "</set>"
        + "WHERE id = #{id}"
        + "</script>")
    void update(userid user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(int id);
}