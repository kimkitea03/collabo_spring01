package com.coffee.controller;

import com.coffee.entity.Product;
import com.coffee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService ;

    @GetMapping("/list") //상품 목록을 List 컬렌션으로 반환해 줍니다.
    public List<Product> list(){
        List<Product> products = this.productService.getProductList() ;

        return products ;
    }
}
