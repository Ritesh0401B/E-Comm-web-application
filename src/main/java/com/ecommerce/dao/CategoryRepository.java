package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

	public boolean existsByName(String name);

	public List<Category> findByIsActiveTrue();
	
}
