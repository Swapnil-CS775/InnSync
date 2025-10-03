package com.innsync.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private Long tableId;
    private List<OrderItemResponseDto> items;
}