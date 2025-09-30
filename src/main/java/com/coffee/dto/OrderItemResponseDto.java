package com.coffee.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderItemResponseDto {
    private Long productId;
    private String productName;
    private int quantity;
}
