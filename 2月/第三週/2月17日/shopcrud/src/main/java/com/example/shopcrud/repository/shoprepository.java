package com.example.shopcrud.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.example.shopcrud.model.shop;

@Mapper
public interface shoprepository {

    @Select("SELECT * FROM products")
    List<shop> findAll();

    @Select("SELECT * FROM products WHERE id = #{id}")
    shop findById(int id);

    @Insert("INSERT INTO products(name, stock, sale_price, purchase_price) VALUES(#{name}, #{stock}, #{salePrice}, #{purchasePrice})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(shop product);

    @Delete("DELETE FROM products WHERE id = #{id}")
    void deleteById(int id);
}
