package com.innsync.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private int status;
}