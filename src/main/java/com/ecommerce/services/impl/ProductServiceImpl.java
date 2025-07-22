package com.ecommerce.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.dao.ProductRepository;
import com.ecommerce.entities.Product;
import com.ecommerce.services.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product saveProduct(Product product) {

		return this.productRepository.save(product);
	}

	@Override
	public List<Product> getAllProduct() {

		return this.productRepository.findAll();
	}

	@Override
	public Product getProductById(int id) {

		Product product = this.productRepository.findById(id).orElse(null);

		return product;

	}

	@Override
	public boolean deleteProduct(int id) {

		Product product = this.productRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(product)) {
			
			this.productRepository.delete(product);
			
			return true;

		}

		return false;

	}

	@Override
	public List<Product> getAllActiveProduct(String category) {
		
		List<Product> product = null;
		
		if(ObjectUtils.isEmpty(category)) {
			
			product = this.productRepository.findByActiveProductTrue();
			
		}else {
			
			product = this.productRepository.findByCategory(category);
			
		}
		
		
		
		return product;
	}

}
