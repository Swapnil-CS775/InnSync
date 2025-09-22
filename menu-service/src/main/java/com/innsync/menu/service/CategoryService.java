package com.innsync.menu.service;

import java.util.List;

import com.innsync.menu.dto.CategoryRequestDto;
import com.innsync.menu.dto.CategoryResponseDto;

public interface CategoryService {
	CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto,Long tenantId);
	
	List<CategoryResponseDto> getAllCategoriesByTenant(Long tenantId);
	
	CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto categoryDto, Long tenantId);
	
	void deleteCategory(Long categoryId, Long tenantId);
}
