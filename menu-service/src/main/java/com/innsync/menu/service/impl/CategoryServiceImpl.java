package com.innsync.menu.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.innsync.menu.dto.CategoryRequestDto;
import com.innsync.menu.dto.CategoryResponseDto;
import com.innsync.menu.entity.MenuCategory;
import com.innsync.menu.exception.ResourceNotFoundException;
import com.innsync.menu.exception.UnauthorizedAccessException;
import com.innsync.menu.repository.MenuCategoryRepository;
import com.innsync.menu.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {


	@Autowired
	private MenuCategoryRepository categoryRepository;

	@Override
	public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto,Long tenantId) {
		// 1. Map the DTO to an Entity
		MenuCategory menuCategory=new MenuCategory();
		menuCategory.setName(categoryRequestDto.getName());
		menuCategory.setDescription(categoryRequestDto.getDescription());
		menuCategory.setTenantId(tenantId); 	// Set the tenant ID for data isolation
		
		// 2. Save the new entity to the database
		MenuCategory savedCategory=categoryRepository.save(menuCategory);
		
		// 3. Map the saved entity back to a Response DTO
		return mapToResponseDto(savedCategory);
	}
	
	@Override
    public List<CategoryResponseDto> getAllCategoriesByTenant(Long tenantId) {
        // 1. Use the new repository method to fetch ONLY the categories for the given tenant
        List<MenuCategory> categories = categoryRepository.findAllByTenantId(tenantId);

        // 2. Map the list of entities to a list of DTOs for the response
        return categories.stream()
                       .map(this::mapToResponseDto) // Uses the existing helper method
                       .collect(Collectors.toList());
    }
	
	
	@Override
    public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto categoryDto, Long tenantId) {
        // 1. Find the existing category by its ID.
        MenuCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        // 2. CRUCIAL: Verify that the category belongs to the authenticated tenant.
        if (!category.getTenantId().equals(tenantId)) {
            throw new UnauthorizedAccessException("User is not authorized to modify this category.");
        }

        // 3. Update the category's properties from the DTO.
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        // 4. Save the updated entity.
        MenuCategory updatedCategory = categoryRepository.save(category);

        // 5. Map and return the updated DTO.
        return mapToResponseDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId, Long tenantId) {
        // 1. Find the existing category by its ID.
        MenuCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        // 2. CRUCIAL: Verify that the category belongs to the authenticated tenant.
        if (!category.getTenantId().equals(tenantId)) {
            throw new SecurityException("User is not authorized to delete this category.");
        }

        // 3. Delete the category.
        categoryRepository.delete(category);
    }
	
	private CategoryResponseDto mapToResponseDto(MenuCategory menuCategory) {
		CategoryResponseDto dto = new CategoryResponseDto();
		dto.setId(menuCategory.getId());
		dto.setName(menuCategory.getName());
		dto.setDescription(menuCategory.getDescription());
		
		return dto;
	}
	
	
}
