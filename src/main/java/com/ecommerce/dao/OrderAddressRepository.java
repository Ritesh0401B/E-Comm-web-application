package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.entities.OrderAddress;
import com.ecommerce.entities.OrderAddress2;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Integer> {

	public List<OrderAddress> findByUserId(int id);

}
