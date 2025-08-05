package com.ecommerce.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.ReviewRepository;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.Review;
import com.ecommerce.services.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {
	
	@Autowired
	private ReviewRepository reviewRepository;

	@Override
	public Review saveReview(Review review) {
		
		Review save = this.reviewRepository.save(review);
		
		if(save != null) {
			return save;
		}
		
		return null;
	}

	@Override
	public List<Review> getReviewsByProduct(Product product) {
		
		List<Review> byProduct = this.reviewRepository.findByProduct(product);
		
		return byProduct;
	}

}
