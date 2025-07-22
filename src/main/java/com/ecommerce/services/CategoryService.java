package com.ecommerce.services;


import java.util.List;

import com.ecommerce.entities.Category;

public interface CategoryService {
	
	public boolean existCategory(String name);
	
	public Category saveCategory(Category category);
	
	public List<Category> getAllCategory();
	
	public Category getCategoryById(int id);
	
	public boolean deleteCategory(int id);
	
	public List<Category> getAllActiveCategories();
	
}
