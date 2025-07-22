package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.entities.MyOrder;

public interface MyOrderRepository extends JpaRepository<MyOrder, Integer> {

	public MyOrder findByOrderId(String orderId);

	
//	@Query("select o from MyOrder o where o.paymentStatus='paid' order by o.orderDate desc")
//	public List<MyOrder> findByStatus();


	public List<MyOrder> findByUserId(int id);


	public List<MyOrder> findByUserIdOrderByOrderDateDesc(int userId);
	

}
