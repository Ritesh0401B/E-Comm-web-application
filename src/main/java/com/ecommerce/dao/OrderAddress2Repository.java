package com.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.entities.OrderAddress2;
import com.ecommerce.entities.User;

public interface OrderAddress2Repository extends JpaRepository<OrderAddress2, Integer> {

	@Query("SELECT o FROM OrderAddress2 o WHERE o.firstName = :firstName AND o.lastName = :lastName AND o.email = :email AND o.mobileNo = :mobileNo AND o.address = :address AND o.city = :city AND o.state = :state AND o.pincode = :pincode AND o.user = :user")
	public OrderAddress2 findByFields(String firstName, String lastName, String email, String mobileNo, String address, String city,
			String state, String pincode, User user);

}
