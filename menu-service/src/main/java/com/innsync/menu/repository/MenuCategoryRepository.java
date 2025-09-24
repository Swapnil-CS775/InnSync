package com.innsync.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.innsync.menu.entity.MenuCategory;

public interface MenuCategoryRepository extends  JpaRepository<MenuCategory, Long>{
	// Spring Data JPA will automatically provide all standard CRUD methods.
    // We can add custom query methods here later if needed.
	List<MenuCategory> findAllByTenantId(Long tenantId);
}
