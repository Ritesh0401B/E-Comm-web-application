package com.ecommerce.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.dao.CartRepository;
import com.ecommerce.dao.ProductRepository;
import com.ecommerce.dao.UserRepository;
import com.ecommerce.entities.Cart;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.User;
import com.ecommerce.services.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Cart saveCart(int productId, int userId) {

		Product product = this.productRepository.findById(productId).orElse(null);

		User user = this.userRepository.findById(userId).orElse(null);

		Cart cartStatus = this.cartRepository.findByProductIdAndUserId(productId, userId);

		Cart cart = null;

		if (ObjectUtils.isEmpty(cartStatus)) {

			cart = new Cart();

			cart.setProduct(product);
			cart.setUser(user);
			cart.setQuantity(1);

			Double totalPrice = product.getDiscountPrice() * 1;
			cart.setTotalPrice(totalPrice);

		} else {

			cart = cartStatus;

			cart.setQuantity(cart.getQuantity() + 1);

			Double totalPrice = product.getDiscountPrice() * cart.getQuantity();
			cart.setTotalPrice(totalPrice);
//			System.out.println(totalPrice);

		}

		Cart save = this.cartRepository.save(cart);

		return save;
	}

	@Override
	public List<Cart> getCartByUser(int userId) {

		List<Cart> carts = this.cartRepository.findByUserId(userId);

		Double subTotal = 0.0;

		List<Cart> updateCarts = new ArrayList<>();

		for (Cart c : carts) {

			Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());

			c.setTotalPrice(totalPrice);

			subTotal += totalPrice;
			c.setSubTotal(subTotal);

			updateCarts.add(c);

//			System.out.println(totalPrice);

		}

		return updateCarts;
	}

	@Override
	public Integer getCountCart(int userId) {

		Integer countByUser = this.cartRepository.countByUser(userId);

		return countByUser;

	}

	@Override
	public void updateQuantityForUser(int id, int pid, int quantity) {

		Cart cart = this.cartRepository.findByProductIdAndUserId(pid, id);

		if (!ObjectUtils.isEmpty(cart)) {
			cart.setQuantity(quantity);

			this.cartRepository.save(cart);

		}

	}

	@Override
	public boolean deleteCartById(int cid) {
		
		boolean existsById = this.cartRepository.existsById(cid);
		
		if(existsById) {
			
			this.cartRepository.deleteById(cid);
			
			return true;
		}
		
		return false;

	}

}
