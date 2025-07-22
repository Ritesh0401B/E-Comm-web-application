package com.ecommerce.services;

import java.util.List;

import com.ecommerce.entities.OrderAddress;
import com.ecommerce.entities.User;

public interface OrderAddressService {
	
	public OrderAddress saveAddress(OrderAddress address);

	public List<OrderAddress> getAddressByUserId(int userId);

	public OrderAddress getAddressById(int addressId);

	public boolean deleteAddress(int aid);

}
