package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.entities.Cart;


public interface CartRepository extends JpaRepository<Cart, Integer> {
	
	public Cart findByProductIdAndUserId(int productId, int userId);

	public List<Cart> findByUserId(int userId);

	@Query("select count(c) from Cart c where c.user.Id = :userId")
	public Integer countByUser(@Param("userId") int userId);
	
	public boolean existsById(int id);

}
