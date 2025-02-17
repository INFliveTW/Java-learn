package com.example.shopcrud.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping; // 修改為 DeleteMapping
import org.springframework.web.bind.annotation.GetMapping; // 修改為 GetMapping
import org.springframework.web.bind.annotation.PathVariable; // 修改為 PathVariable
import org.springframework.web.bind.annotation.PostMapping; // 修改為 PostMapping
import org.springframework.web.bind.annotation.RequestBody; // 修改為 RequestBody
import org.springframework.web.bind.annotation.RequestMapping; // 修改為 RequestMapping
import org.springframework.web.bind.annotation.RestController; // 修改為 RestController

import com.example.shopcrud.model.shop; // 修改為 shop
import com.example.shopcrud.service.shopservice; // 修改為 shopservice

@RestController // 修改為 RestController
@RequestMapping("/products")  // 修改路徑為 products
public class shopcontroller { // 修改為 shopcontroller
    private final shopservice shopservice; // 修改為 shopservice

    public shopcontroller(shopservice shopservice) { // 修改為 shopcontroller
        this.shopservice = shopservice; // 修改為 shopservice
    } // 修改為 shopcontroller

    @GetMapping
    public List<shop> getAllProducts() {
        return shopservice.getAllProducts();
    }

    @GetMapping("/{id}")
    public shop getProductById(@PathVariable int id) {
        return shopservice.getProductById(id);
    }

    @PostMapping
    public void addProduct(@RequestBody shop product) {
        shopservice.addProduct(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        shopservice.deleteProduct(id);
    }
}