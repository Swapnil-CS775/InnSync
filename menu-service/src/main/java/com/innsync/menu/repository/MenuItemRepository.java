package com.innsync.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.innsync.menu.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long>{
	// Standard CRUD methods will be available automatically.
	// Custom query method to find all items within a specific category for a tenant
    List<MenuItem> findByCategory_IdAndTenantId(Long categoryId, Long tenantId);

    // Custom query method to find all items for a specific tenant
    List<MenuItem> findAllByTenantId(Long tenantId);
}
