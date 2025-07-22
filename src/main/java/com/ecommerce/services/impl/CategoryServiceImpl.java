package com.ecommerce.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.dao.CategoryRepository;
import com.ecommerce.entities.Category;
import com.ecommerce.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public boolean existCategory(String name) {
		
		return this.categoryRepository.existsByName(name);
		
	}

	@Override
	public Category saveCategory(Category category) {

		return categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategory() {

		return categoryRepository.findAll();
	}

	@Override
	public boolean deleteCategory(int id) {
		
		Category category = categoryRepository.findById(id).orElse(null);
		
		if(!ObjectUtils.isEmpty(category)) {
			
			categoryRepository.delete(category);
			
			return true;
			
		}
		
		return false;
	}

	@Override
	public Category getCategoryById(int id) {
		
		Category category= categoryRepository.findById(id).orElse(null);
		
		return category;
	}

	@Override
	public List<Category> getAllActiveCategories() {
		
		List<Category> categories = this.categoryRepository.findByIsActiveTrue();
		
		return categories;
	}

}
