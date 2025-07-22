package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{
	
	public List<Product> findByActiveProductTrue();

	public List<Product> findByCategory(String category);

}
