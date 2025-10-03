package com.innsync.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableResponseDto {
    private Long id;
    private String tableNumber;
    private String qrCodeIdentifier;
    private Long tenantId;
}