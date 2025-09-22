package com.innsync.menu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {
	@NotBlank(message="Category name cannot be blank")
	private String name;
	private String description;
}
