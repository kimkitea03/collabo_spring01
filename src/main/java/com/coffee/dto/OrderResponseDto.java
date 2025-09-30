package com.coffee.dto;

import com.coffee.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class OrderResponseDto {
    private Long orderId;
    private LocalDate orderDate;
    private OrderStatus status;
    private List<OrderItemResponseDto> orderProducts;
}
