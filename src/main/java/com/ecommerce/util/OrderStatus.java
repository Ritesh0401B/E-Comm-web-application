package com.ecommerce.util;

import java.util.List;

public enum OrderStatus {

	IN_PROGRESS(1, "In Progress"), ORDER_RECEIVED(2, "Order Received"), PRODUCT_PACKED(3, "Product Packed"),
	OUT_FOR_DELIVERY(4, "Out for Delivery"), DELIVERED(5, "Delivered"), CANCEL(6, "Cancelled"), SUCCESS(7, "Success");

	private int id;

	private String name;

	private OrderStatus() {
	}

	private OrderStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static List<OrderStatus> getTrackingOrder() {

		return List.of(IN_PROGRESS, ORDER_RECEIVED, PRODUCT_PACKED, OUT_FOR_DELIVERY, DELIVERED);
	}
	
	public static List<OrderStatus> getTrackingOrders() {

		return List.of(IN_PROGRESS, ORDER_RECEIVED, PRODUCT_PACKED, OUT_FOR_DELIVERY, DELIVERED, CANCEL);
	}
	
	public static int getStatusIndex(String status){
		
		int i=0;
		for(OrderStatus s : getTrackingOrder()) {
			if(s.getName().equalsIgnoreCase(status)) return i;
			i++;
		}
		
		return -1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
