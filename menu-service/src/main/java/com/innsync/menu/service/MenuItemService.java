package com.innsync.menu.service;

import java.util.List;

import com.innsync.menu.dto.ItemRequestDto;
import com.innsync.menu.dto.ItemResponseDto;

public interface MenuItemService {
	ItemResponseDto createItem(Long tenantId, ItemRequestDto itemRequestDto);
    List<ItemResponseDto> getAllItemsByTenant(Long tenantId);
    
    ItemResponseDto updateItem(Long tenantId, Long itemId, ItemRequestDto itemRequestDto);

    void deleteItem(Long tenantId, Long itemId);
    
    ItemResponseDto getPublicItemById(Long itemId);
}
