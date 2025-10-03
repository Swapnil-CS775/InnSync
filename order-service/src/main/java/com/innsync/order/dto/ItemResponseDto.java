package com.innsync.order.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private boolean isAvailable;
    private Long categoryId;
    private List<String> imageUrls = new ArrayList<>();
}