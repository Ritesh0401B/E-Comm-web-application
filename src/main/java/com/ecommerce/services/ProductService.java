package com.ecommerce.services;



import java.util.List;

import com.ecommerce.entities.Product;

public interface ProductService {
	
	public Product saveProduct(Product product);
	
	public List<Product> getAllProduct();
	
	public Product getProductById(int id);
	
	public boolean deleteProduct(int id);
	
	public List<Product> getAllActiveProduct(String category);

}
