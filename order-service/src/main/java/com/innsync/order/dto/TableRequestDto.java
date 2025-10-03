package com.innsync.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableRequestDto {
    @NotBlank
    private String tableNumber;
}