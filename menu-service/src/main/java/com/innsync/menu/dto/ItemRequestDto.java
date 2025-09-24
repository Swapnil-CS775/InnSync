package com.innsync.menu.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequestDto {
    @NotBlank
    private String name;
    private String description;
    @NotNull @PositiveOrZero
    private Double price;
    private boolean isAvailable = true;
    @NotNull
    private Long categoryId; // The ID of the category it belongs to
    private List<String> imageUrls = new ArrayList<>();
}