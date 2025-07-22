package com.ecommerce.services;

import java.util.List;
import com.ecommerce.entities.MyOrder;
import com.ecommerce.entities.User;

public interface MyOrderService {
	
	public MyOrder savaOrder(User user, int address, String price, String paymentType);

	public MyOrder save(MyOrder myOrder);

	public MyOrder getOrderByOrderId(String orderId);

	public List<MyOrder> getOrderByUserId(int userId);

	public MyOrder savePayment(MyOrder order);
	
	public MyOrder getOrderById(int id);

	public MyOrder updateOrder(MyOrder order);

	public List<MyOrder> getAllOrders();

}
