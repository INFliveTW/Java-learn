package com.example.shopcrud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.shopcrud.model.shop;
import com.example.shopcrud.repository.shoprepository;

@Service
public class shopservice {
    private final shoprepository shoprepository;

    public shopservice(shoprepository shoprepository) {
        this.shoprepository = shoprepository;
    }

    // 取得所有商品
    public List<shop> getAllProducts() {
        return shoprepository.findAll();
    }

    // 透過 ID 取得單個商品
    public shop getProductById(int id) {
        return shoprepository.findById(id);
    }

    // 新增商品
    public void addProduct(shop product) {
        shoprepository.save(product);
    }

    // 刪除商品
    public void deleteProduct(int id) {
        shoprepository.deleteById(id);
    }
}
