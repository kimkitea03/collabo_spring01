package com.coffee.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Element {
    private int id;
    private String name ;
    private int price;
    private String category ;
    private int stock;
    private String image ;
    private String description ;

    public Element() {
    }

    public Element(int id, String name, int price, String category, int stock, String image, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.image = image;
        this.description = description;
    }
}