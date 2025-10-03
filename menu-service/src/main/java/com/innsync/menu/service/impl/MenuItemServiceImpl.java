package com.innsync.menu.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import com.innsync.menu.dto.ItemRequestDto;
import com.innsync.menu.dto.ItemResponseDto;
import com.innsync.menu.entity.MenuCategory;
import com.innsync.menu.entity.MenuItem;
import com.innsync.menu.exception.ResourceNotFoundException;
import com.innsync.menu.repository.MenuCategoryRepository;
import com.innsync.menu.repository.MenuItemRepository;
import com.innsync.menu.service.MenuItemService;


@Service
public class MenuItemServiceImpl implements MenuItemService{
	
	@Autowired
    private MenuItemRepository menuItemRepository;
	
	@Autowired
    private MenuCategoryRepository menuCategoryRepository;
	

	@Override
	public ItemResponseDto createItem(Long tenantId, ItemRequestDto itemRequestDto) {
		// 1. Find the parent category
        MenuCategory category = menuCategoryRepository.findById(itemRequestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 2. Security Check: Ensure the category belongs to the correct tenant
        if (!category.getTenantId().equals(tenantId)) {
            throw new SecurityException("Category does not belong to the current tenant");
        }

        // 3. Map DTO to Entity
        MenuItem menuItem = new MenuItem();
        menuItem.setName(itemRequestDto.getName());
        menuItem.setDescription(itemRequestDto.getDescription());
        menuItem.setPrice(itemRequestDto.getPrice());
        menuItem.setAvailable(itemRequestDto.isAvailable());
        menuItem.setTenantId(tenantId);
        menuItem.setCategory(category); // Set the relationship

        // 4. Save and return DTO
        MenuItem savedItem = menuItemRepository.save(menuItem);
        return mapToItemResponseDto(savedItem);
	}

	@Override
	public List<ItemResponseDto> getAllItemsByTenant(Long tenantId) {
		return menuItemRepository.findAllByTenantId(tenantId).stream()
                .map(this::mapToItemResponseDto)
                .collect(Collectors.toList());
	}
	
	
	private ItemResponseDto mapToItemResponseDto(MenuItem menuItem) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setCategoryId(menuItem.getCategory().getId());
        dto.setImageUrls(new ArrayList<>(menuItem.getImageUrls()));
        return dto;
    }
	
	
	@PutMapping("/{id}")
	public ItemResponseDto updateItem(Long tenantId, Long itemId, ItemRequestDto dto) {
	    // 1. Fetch the existing MenuItem from the database. It is now a 'managed' entity.
	    MenuItem menuItem = menuItemRepository.findById(itemId)
	            .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

	    // 2. Security Check: Verify the owner.
	    if (!menuItem.getTenantId().equals(tenantId)) {
	        throw new SecurityException("Not authorized to update this item.");
	    }

	    // 3. Update all the properties by calling the setters.
	    menuItem.setName(dto.getName());
	    menuItem.setDescription(dto.getDescription());
	    menuItem.setPrice(dto.getPrice());
	    menuItem.setAvailable(dto.isAvailable());

	    // Update the category relationship
	    MenuCategory newCategory = menuCategoryRepository.findById(dto.getCategoryId()).orElseThrow();
	    menuItem.setCategory(newCategory);
	    
	    // Update the list of images
	    menuItem.getImageUrls().clear();
	    menuItem.getImageUrls().addAll(dto.getImageUrls()); // Assuming imageUrls are in the DTO

	    // 4. Save the entity. JPA will automatically generate the UPDATE SQL.
	    MenuItem updatedItem = menuItemRepository.save(menuItem);

	    // 5. Return the updated data.
	    return mapToItemResponseDto(updatedItem);
	}
	
	@Override // This annotation confirms we are fulfilling the interface contract.
	public void deleteItem(Long tenantId, Long itemId) {
	    // 1. Find the item to delete.
	    MenuItem menuItem = menuItemRepository.findById(itemId)
	            .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + itemId));

	    // 2. Security Check: Verify the item belongs to the authenticated tenant.
	    if (!menuItem.getTenantId().equals(tenantId)) {
	        throw new SecurityException("User is not authorized to delete this item.");
	    }

	    // 3. If the check passes, delete the item.
	    menuItemRepository.delete(menuItem);
	}

	@Override
	public ItemResponseDto getPublicItemById(Long itemId) {
		MenuItem menuItem = menuItemRepository.findById(itemId)
	            .orElseThrow(() -> new RuntimeException("MenuItem not found"));
	    return mapToItemResponseDto(menuItem);
	}
	
	

}
