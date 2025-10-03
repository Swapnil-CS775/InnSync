package com.innsync.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddItemRequestDto {
    private Long menuItemId;
    private int quantity;
}