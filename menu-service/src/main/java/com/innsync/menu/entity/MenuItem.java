package com.innsync.menu.entity;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="menu_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuItem {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	private String description;
	
	@ElementCollection   // 1. Tells JPA this is a collection of simple elements.
	@CollectionTable(
				name="menu_item_images", 			// 2. Specifies the name of the new table to be created.
				joinColumns = @JoinColumn(name="menu_item_id")		// 3. Specifies the foreign key column in that table.
			)
	@Column(name = "image_url",nullable = false)
	private List<String>imageUrls= new ArrayList<>();
	
	
	@Column(nullable = false)
	private double price;
	
	
	@Column(name="is_available")
	private boolean isAvailable=true;
	
	@Column(nullable = false,name="tenant_id")
	private Long tenantId;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id",nullable = false)
	private MenuCategory category;
}











