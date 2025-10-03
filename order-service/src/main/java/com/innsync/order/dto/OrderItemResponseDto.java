package com.innsync.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponseDto {
    private Long id;
    private String itemName;
    private int quantity;
    private BigDecimal priceAtOrder;
}