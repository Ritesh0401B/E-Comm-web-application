package com.ecommerce.services;

import java.util.List;

import com.ecommerce.entities.Product;
import com.ecommerce.entities.Review;

public interface ReviewService {
	
	public Review saveReview(Review review);
	
	 public List<Review> getReviewsByProduct(Product product);

}
