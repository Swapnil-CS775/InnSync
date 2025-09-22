package com.innsync.menu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innsync.menu.dto.ItemRequestDto;
import com.innsync.menu.dto.ItemResponseDto;
import com.innsync.menu.service.MenuItemService;
import com.innsync.menu.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menu/items")
public class MenuItemController {
	@Autowired
    private MenuItemService menuItemService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {

        Long tenantId = jwtUtil.extractTenantId(token.substring(7));
        ItemResponseDto createdItem = menuItemService.createItem(tenantId, itemRequestDto);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems(
            @RequestHeader("Authorization") String token) {

        Long tenantId = jwtUtil.extractTenantId(token.substring(7));
        List<ItemResponseDto> items = menuItemService.getAllItemsByTenant(tenantId);
        return ResponseEntity.ok(items);
    }
    
 // Endpoint to UPDATE an existing menu item
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        
        Long tenantId = jwtUtil.extractTenantId(token.substring(7));
        ItemResponseDto updatedItem = menuItemService.updateItem(tenantId, id, itemRequestDto);
        return ResponseEntity.ok(updatedItem);
    }

    // Endpoint to DELETE a menu item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        
        Long tenantId = jwtUtil.extractTenantId(token.substring(7));
        menuItemService.deleteItem(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}
