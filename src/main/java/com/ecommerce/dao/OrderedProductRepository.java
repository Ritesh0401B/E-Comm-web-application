package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.entities.OrderedProduct;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Integer> {

}
