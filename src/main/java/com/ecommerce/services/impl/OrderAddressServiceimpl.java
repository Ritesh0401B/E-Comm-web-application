package com.ecommerce.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.dao.MyOrderRepository;
import com.ecommerce.dao.OrderAddressRepository;
import com.ecommerce.entities.OrderAddress;
import com.ecommerce.services.OrderAddressService;

@Service
public class OrderAddressServiceimpl implements OrderAddressService {

    private final MyOrderRepository myOrderRepository;
	
	@Autowired
	private OrderAddressRepository orderAddressRepository;

    OrderAddressServiceimpl(MyOrderRepository myOrderRepository) {
        this.myOrderRepository = myOrderRepository;
    }

	@Override
	public OrderAddress saveAddress(OrderAddress address) {
		
		OrderAddress save = this.orderAddressRepository.save(address);
		
		System.out.println(save);
		
		return save;
	}

	@Override
	public List<OrderAddress> getAddressByUserId(int userId) {
		
		List<OrderAddress> byUserId = this.orderAddressRepository.findByUserId(userId);
		
		return byUserId;
		
	
	}

	@Override
	public OrderAddress getAddressById(int addressId) {
		
		OrderAddress address = this.orderAddressRepository.findById(addressId).orElse(null);
		
		return address;
		
	}

	@Override
	public boolean deleteAddress(int aid) {
		
	    boolean existsById = this.orderAddressRepository.existsById(aid);
	    
	    if(existsById) {
	    	
	    	this.orderAddressRepository.deleteById(aid);
	    	
	    	return true;
	    }
		
		return false;
	}

	

}
