package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.entities.Product;
import com.ecommerce.entities.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	public List<Review> findByProduct(Product product);

}
