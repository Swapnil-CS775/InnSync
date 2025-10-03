package com.innsync.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StartOrderResponseDto {
    private Long orderId;
    private String guestToken; // The temporary JWT for the customer's session
}