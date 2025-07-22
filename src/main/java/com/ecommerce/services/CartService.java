package com.ecommerce.services;

import java.util.List;

import com.ecommerce.entities.Cart;

public interface CartService {
	
	public Cart saveCart(int productId, int userId);
	
	public List<Cart> getCartByUser(int userId);

	public Integer getCountCart(int userId);

	public void updateQuantityForUser(int id, int pid, int quantity);

	public boolean deleteCartById(int cid);

}
