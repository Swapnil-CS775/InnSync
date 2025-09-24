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

import com.innsync.menu.dto.CategoryRequestDto;
import com.innsync.menu.dto.CategoryResponseDto;
import com.innsync.menu.service.CategoryService;
import com.innsync.menu.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menu/categories")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@PostMapping
	public ResponseEntity<CategoryResponseDto> createCategory(
				@RequestHeader("Authorization") String authorizationHeader,
				@Valid @RequestBody CategoryRequestDto categoryRequestDto
			){
		// 1. Extract the token from the "Bearer <token>" header string.
		String token = authorizationHeader.substring(7);
		
		// 2. Use our JwtUtil to extract the tenantId from the token's claims.
		Long tenantId = jwtUtil.extractTenantId(token);
		
		// 3. Call the service, passing the request data and the trusted tenantId.
		CategoryResponseDto createdCategory = categoryService.createCategory(categoryRequestDto, tenantId);
		
		// 4. Return a "201 Created" status with the new category's data.
		return new ResponseEntity<>(createdCategory,HttpStatus.CREATED);
	}
	
	@GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(
            @RequestHeader("Authorization") String authorizationHeader) {

        String jwt = authorizationHeader.substring(7);
        Long tenantId = jwtUtil.extractTenantId(jwt);

        List<CategoryResponseDto> categories = categoryService.getAllCategoriesByTenant(tenantId);

        return ResponseEntity.ok(categories);
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto categoryRequestDto) {

        String jwt = authorizationHeader.substring(7);
        Long tenantId = jwtUtil.extractTenantId(jwt);

        CategoryResponseDto updatedCategory = categoryService.updateCategory(id, categoryRequestDto, tenantId);

        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id) {

        String jwt = authorizationHeader.substring(7);
        Long tenantId = jwtUtil.extractTenantId(jwt);

        categoryService.deleteCategory(id, tenantId);

        // Return a 204 No Content status, which is standard for a successful delete.
        return ResponseEntity.noContent().build();
    }
}


/*
 * =====================================================================================
 * Code Explanation
 * =====================================================================================
 *
 * This file defines the CategoryController, which acts as the API layer for managing
 * menu categories. Its primary role is to handle incoming HTTP requests from the client
 * (like a React frontend or Postman), delegate the business logic to the CategoryService,
 * and then return a structured HTTP response.
 *
 * --- Annotations ---
 * @RestController: Marks this class as a RESTful controller, which means the return
 * value of its methods will be automatically converted to JSON.
 * @RequestMapping("/api/menu/categories"): Sets the base URL path for all endpoints
 * defined within this class.
 * @Autowired:      Performs dependency injection. Spring automatically provides the
 * instances for CategoryService and JwtUtil.
 *
 * --- createCategory Method ---
 * This method is responsible for handling the creation of a new menu category.
 *
 * @PostMapping: Maps this method to handle HTTP POST requests sent to the base URL
 * (/api/menu/categories).
 *
 * The method performs the following steps:
 * 1.  Receives the Request: It takes the category details (name, description) from the
 * HTTP request's JSON body (@RequestBody) and the user's authentication token
 * from the HTTP header (@RequestHeader).
 *
 * 2.  Ensures Security & Multi-Tenancy: It extracts the JWT from the "Authorization"
 * header. It then uses the JwtUtil to parse this token and extract the 'tenantId'
 * claim. This is the most critical step for security, as it identifies which
 * business this request belongs to.
 *
 * 3.  Delegates Business Logic: It passes the category data and the trusted 'tenantId'
 * to the CategoryService. The service is responsible for the actual work of
 * creating and saving the new category in the database.
 *
 * 4.  Returns a Response: After the service successfully creates the category, this
 * method wraps the result in a ResponseEntity and sends it back to the client with
 * a "201 Created" HTTP status, confirming that the operation was successful.
 *
 */