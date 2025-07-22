package com.ecommerce.services.impl;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.ecommerce.dao.*;
import com.ecommerce.entities.Cart;
import com.ecommerce.entities.MyOrder;
import com.ecommerce.entities.OrderAddress;
import com.ecommerce.entities.OrderAddress2;
import com.ecommerce.entities.OrderedProduct;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.User;
import com.ecommerce.services.CartService;
import com.ecommerce.services.MyOrderService;
import com.ecommerce.services.OrderAddressService;
import com.ecommerce.services.UserService;
import com.ecommerce.util.CommonUtil;
import com.ecommerce.util.OrderStatus;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class MyOrderServiceImpl implements MyOrderService {

    private final OrderAddress2Repository orderAddress2Repository;

	private final UserRepository userRepository;

	private final OrderAddressRepository orderAddressRepository;

	private final MyOrderRepository myOrderRepository;

	private final CartRepository cartRepository;
	
	private final CommonUtil commonUtil;

	MyOrderServiceImpl(CartServiceImpl cartServiceImpl, CartRepository cartRepository,
			MyOrderRepository myOrderRepository, OrderAddressRepository orderAddressRepository,
			UserRepository userRepository, CommonUtil commonUtil, OrderAddress2Repository orderAddress2Repository) {
		this.cartRepository = cartRepository;
		this.myOrderRepository = myOrderRepository;
		this.orderAddressRepository = orderAddressRepository;
		this.userRepository = userRepository;
		this.commonUtil = commonUtil;
		this.orderAddress2Repository = orderAddress2Repository;
	}

	@Override
	public MyOrder savaOrder(User user, int address, String price, String paymentType) {

		List<Cart> carts = this.cartRepository.findByUserId(user.getId());

		User user1 = this.userRepository.findById(user.getId()).orElse(null);

		MyOrder save = null;

		MyOrder order = new MyOrder();

		order.setOrderId(UUID.randomUUID().toString());
		order.setOrderDate(LocalDateTime.now());
		order.setPrice(Double.parseDouble(price));
		order.setUser(user1);

//			String receipt = "txn_" + UUID.randomUUID().toString();
		order.setReceipt(null);

		OrderAddress orderAddress = this.orderAddressRepository.findById(address).orElse(null);
		System.out.println("myoid" + orderAddress);
		
		OrderAddress2 existing = this.orderAddress2Repository.findByFields(orderAddress.getFirstName(), orderAddress.getLastName(), orderAddress.getEmail(),
                orderAddress.getMobileNo(), orderAddress.getAddress(), orderAddress.getCity(),
                orderAddress.getState(), orderAddress.getPincode(), orderAddress.getUser());
		
		OrderAddress2 oa2;
		
		if(existing != null) {
			oa2 = existing;
		}else {
			oa2 = new OrderAddress2();
			
			oa2.setFirstName(orderAddress.getFirstName());
			oa2.setLastName(orderAddress.getLastName());
			oa2.setEmail(orderAddress.getEmail());
			oa2.setMobileNo(orderAddress.getMobileNo());
			oa2.setAddress(orderAddress.getAddress());
			oa2.setCity(orderAddress.getCity());
			oa2.setState(orderAddress.getState());
			oa2.setPincode(orderAddress.getPincode());
			oa2.setUser(orderAddress.getUser());
			
			oa2 = this.orderAddress2Repository.save(oa2);
		}
		
		order.setOrderAddress2(oa2);

		order.setOrderStatus(OrderStatus.IN_PROGRESS.getName());
		order.setPaymentType(paymentType);

		List<OrderedProduct> opp = new ArrayList<>();

		for (Cart cart2 : carts) {
			OrderedProduct op = new OrderedProduct();
			op.setMyOrder(order);
			op.setPrice(cart2.getProduct().getDiscountPrice());
			op.setTotalPrice(cart2.getProduct().getDiscountPrice() * cart2.getQuantity());
			op.setProduct(cart2.getProduct());
			op.setQuantity(cart2.getQuantity());

			opp.add(op);
		}
		order.setOrderedProducts(opp);

		save = this.myOrderRepository.save(order);

		if (save != null) {
			
			try {
				commonUtil.sendMailForProductOrder(save, "Success");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			resetCart(user.getId());
		}

		return save;
	}

	private void resetCart(int userId) {
		List<Cart> byUserId = this.cartRepository.findByUserId(userId);

		for (Cart cart : byUserId) {
			this.cartRepository.delete(cart);
		}

	}

	@Override
	public MyOrder save(MyOrder myOrder) {

		MyOrder save = this.myOrderRepository.save(myOrder);

		if (save != null) {

			resetCart(save.getUser().getId());

		}

		return save;
	}
	
	@Override
	public MyOrder updateOrder(MyOrder myOrder) {
		
		MyOrder save = this.myOrderRepository.save(myOrder);
		
		return save;
	}
	
	@Override
	public MyOrder getOrderById(int id) {
		
		MyOrder order = this.myOrderRepository.findById(id).orElse(null);
		
		return order;
	}

	@Override
	public MyOrder getOrderByOrderId(String orderId) {

		MyOrder byOrderId = this.myOrderRepository.findByOrderId(orderId);

		return byOrderId;
	}


	@Override
	public List<MyOrder> getOrderByUserId(int userId) {

		List<MyOrder> myOrder = this.myOrderRepository.findByUserIdOrderByOrderDateDesc(userId);

		return myOrder;
	}

	@Override
	public MyOrder savePayment(MyOrder order) {
		
		MyOrder save = this.myOrderRepository.save(order);

		if (save != null) {
			
			resetCart(save.getUser().getId());
			
			return save;

		}

		return null;
	}

	@Override
	public List<MyOrder> getAllOrders() {
		
		List<MyOrder> orders = this.myOrderRepository.findAll();
		
		return orders;
	}

	
}
