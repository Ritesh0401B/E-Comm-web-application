package com.ecommerce.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ecommerce.dao.*;
import com.ecommerce.entities.Cart;
import com.ecommerce.entities.Category;
import com.ecommerce.entities.MyOrder;
import com.ecommerce.entities.OrderAddress;
import com.ecommerce.entities.OrderAddress2;
import com.ecommerce.entities.OrderedProduct;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.Review;
import com.ecommerce.entities.User;
import com.ecommerce.services.CartService;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.MyOrderService;
import com.ecommerce.services.OrderAddressService;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.ReviewService;
import com.ecommerce.services.UserService;
import com.ecommerce.util.CommonUtil;
import com.ecommerce.util.Message;
import com.ecommerce.util.OrderStatus;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
@RequestMapping("/user")
public class UserController /* ~~(Could not parse as Java)~~> */ {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

	@Autowired
	private OrderedProductRepository orderedProductRepository;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderAddressService orderAddressService;

	@Autowired
	private MyOrderService myOrderService;
	
	@Autowired
	private ReviewService reviewService;

	@Autowired
	private CommonUtil commonUtil;

	private OrderAddress2Repository orderAddress2Repository;

	UserController(OrderAddressRepository orderAddressRepository, OrderAddress2Repository orderAddress2Repository_1, UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		if (principal != null) {

			String email = principal.getName();

			User user = this.userService.getUserByEmail(email);

			model.addAttribute("user", user);

			Integer countCart = this.cartService.getCountCart(user.getId());

			model.addAttribute("countCart", countCart);

		}

		List<Category> allActiveCategories = this.categoryService.getAllActiveCategories();
		model.addAttribute("categories", allActiveCategories);

	}

	@GetMapping("/index")
	public String home() {

		return "User/index";
	}

	@GetMapping("/add-cart")
	public String addToCart(@RequestParam int pid, @RequestParam int uid, RedirectAttributes redirectAttributes) {

		Cart saveCart = this.cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			redirectAttributes.addFlashAttribute("message", new Message("Product add to cart failed", "text-danger"));
		} else {
			redirectAttributes.addFlashAttribute("message", new Message("Product added to cart", "text-success"));

			Product product = this.productService.getProductById(pid);

			product.setStock(product.getStock() - saveCart.getQuantity());

			this.productService.saveProduct(product);

			System.out.println(saveCart.getTotalPrice());
		}

		return "redirect:/product/" + pid;
	}

	@GetMapping("/view-cart")
	public String viewCart(@RequestParam int uid, Model model) {

		model.addAttribute("title", "View Cart");

//		System.out.println(uid);

		List<Cart> cart = this.cartService.getCartByUser(uid);

		if (!cart.isEmpty()) {

//		for (Cart c : cart) {
//
//			Double subTotal = c.getSubTotal();
//
//			model.addAttribute("subTotal", subTotal);
//
//		}

			Double subTotal = cart.get(cart.size() - 1).getSubTotal();
			String formattedSubTotal = String.format("%.2f", subTotal);
			model.addAttribute("subTotal", formattedSubTotal);

//		System.out.println(cart);

			model.addAttribute("cart", cart);

		}

		return "User/cart";
	}

	@PostMapping("/update-cart")
	public String updateCart(@RequestParam("productId") int pid, @RequestParam("quantity") int quantity,
			@RequestParam("cid") int cid, @RequestParam("action") String action, Principal principal) {

//		System.out.println(pid);
		System.out.println(quantity);
//		System.out.println(action);

		String name = principal.getName();
		User userByEmail = this.userService.getUserByEmail(name);
		int id = userByEmail.getId();

		System.out.println(id);

		if (action.equals("increase") && quantity < 10) {
			quantity++;
			this.cartService.updateQuantityForUser(id, pid, quantity);

			Product product = this.productService.getProductById(pid);
			product.setStock(product.getStock() - 1);

			this.productService.saveProduct(product);

		} else if (action.equals("decrease") && quantity >= 1) {
			quantity--;
			this.cartService.updateQuantityForUser(id, pid, quantity);

			Product product = this.productService.getProductById(pid);
			product.setStock(product.getStock() + 1);

			this.productService.saveProduct(product);

		}

		if (quantity == 0) {
			this.cartService.deleteCartById(cid);
		}

		return "redirect:/user/view-cart?uid=" + id;
	}

	@GetMapping("/delete-cart")
	public String deleteCart(@RequestParam("cid") int cid, @RequestParam("q") int q, @RequestParam("pid") int pid,
			Principal p) {

		System.out.println(cid);
		System.out.println(q);
		System.out.println(pid);

		boolean deleteCartById = this.cartService.deleteCartById(cid);

		if (deleteCartById) {
			Product product = this.productService.getProductById(pid);
			product.setStock(product.getStock() + q);

			this.productService.saveProduct(product);
		}

		User user = this.userService.getUserByEmail(p.getName());
		int id = user.getId();

		return "redirect:/user/view-cart?uid=" + id;
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model model) {

		String name = p.getName();
		User userByUserName = this.userService.getUserByEmail(name);

		List<Cart> cart = this.cartService.getCartByUser(userByUserName.getId());

		List<OrderAddress> addressByUserId = this.orderAddressService.getAddressByUserId(userByUserName.getId());

		model.addAttribute("cart", cart);
		model.addAttribute("address", addressByUserId);

		if (!cart.isEmpty()) {
			Double totalPrice = cart.get(cart.size() - 1).getSubTotal();

			Double gstt = ((totalPrice * 3) / 100);

			long gst = Math.round(gstt);

			Double totalOrderPrice = totalPrice + 25 + gst;

			model.addAttribute("gst", gst);
			model.addAttribute("totalPrice", totalPrice);
			model.addAttribute("totalOrderPrice", totalOrderPrice);
		} else {
			model.addAttribute("totalPrice", 0.0);
			model.addAttribute("totalOrderPrice", 0.0);
		}

		return "User/order";
	}

	@PostMapping("/save-order")
	public String saveOrder(@RequestParam String paymentType, @RequestParam int selectedAddress,
			@RequestParam String totalOrderPrice, Principal p) {

		System.out.println(totalOrderPrice);
		System.out.println(paymentType);
		System.out.println(selectedAddress);

		String email = p.getName();

		User userByUserName = this.userService.getUserByEmail(email);

		this.myOrderService.savaOrder(userByUserName, selectedAddress, totalOrderPrice, paymentType);

		return "redirect:/user/order-success";
	}

	// creating order for payment

	@PostMapping("/create-order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) {

		System.out.println("Order function executed");
		System.out.println(data);

		User user = this.userService.getUserByEmail(principal.getName());

		List<Cart> cart = this.cartService.getCartByUser(user.getId());

		int amt = (int) (Double.parseDouble(data.get("amount").toString()) * 100);
		String paymentType = (String) data.get("paymentType");

		int addressId = Integer.parseInt(data.get("selectedAddress").toString());
		OrderAddress address = this.orderAddressService.getAddressById(addressId);
		System.out.println(addressId);
		System.out.println("oaid" + address);

		try {

			RazorpayClient client = new RazorpayClient("rzp_test_fxip5V4ioepJdE", "DgsJqecK6dPSozc0rxMlzL42");

			JSONObject paymentRequest = new JSONObject();

			paymentRequest.put("amount", amt);
			paymentRequest.put("currency", "INR");

			String receipt = "txn_" + UUID.randomUUID().toString();
			paymentRequest.put("receipt", receipt);

			// creating new order

			Order order = client.orders.create(paymentRequest);

			// save the order in Database

			MyOrder myOrder = new MyOrder();

			myOrder.setOrderId(order.get("id"));
			System.out.println("ORDER_ID = " + myOrder.getOrderId());

//			order_QoiUF5oF7xSXFs

			myOrder.setPrice(Double.parseDouble(order.get("amount").toString()) / 100.0);
			myOrder.setReceipt(order.get("receipt"));
			myOrder.setPaymentId(null);
			myOrder.setPaymentStatus("created");
			myOrder.setOrderStatus(OrderStatus.IN_PROGRESS.getName());

			OrderAddress2 existing = this.orderAddress2Repository.findByFields(address.getFirstName(),
					address.getLastName(), address.getEmail(), address.getMobileNo(), address.getAddress(),
					address.getCity(), address.getState(), address.getPincode(), address.getUser());

			OrderAddress2 oa2;

			if (existing != null) {

				oa2 = existing;

			} else {
				oa2 = new OrderAddress2();

				oa2.setFirstName(address.getFirstName());
				oa2.setLastName(address.getLastName());
				oa2.setEmail(address.getEmail());
				oa2.setMobileNo(address.getMobileNo());
				oa2.setAddress(address.getAddress());
				oa2.setCity(address.getCity());
				oa2.setState(address.getState());
				oa2.setPincode(address.getPincode());
				oa2.setUser(address.getUser());

				oa2 = this.orderAddress2Repository.save(oa2);
			}

			myOrder.setOrderAddress2(oa2);
			myOrder.setOrderDate(LocalDateTime.now());
			myOrder.setPaymentType(paymentType);

			myOrder.setUser(user);

			List<OrderedProduct> opp = new ArrayList<>();

			for (Cart cart2 : cart) {
				OrderedProduct op = new OrderedProduct();
				op.setMyOrder(myOrder);
				op.setPrice(cart2.getProduct().getPrice());
				op.setTotalPrice(cart2.getProduct().getPrice() * cart2.getQuantity());
				op.setProduct(cart2.getProduct());
				op.setQuantity(cart2.getQuantity());

				opp.add(op);
			}
			myOrder.setOrderedProducts(opp);

			this.myOrderService.savePayment(myOrder);

//			System.out.println("ORDER = " + order);

			return order.toString();

		} catch (RazorpayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	@PostMapping("/update-order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {

		System.out.println(data);

		MyOrder myOrder = this.myOrderService.getOrderByOrderId(data.get("order_id").toString());

		myOrder.setPaymentId(data.get("payment_id").toString());

		myOrder.setPaymentStatus(data.get("status").toString());

		MyOrder save = this.myOrderService.save(myOrder);

		if (save != null) {

			try {
				System.out.println("Sending email = " + save.getOrderAddress2().getEmail());

				commonUtil.sendMailForProductOrder(save, "Success");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return ResponseEntity.ok(Map.of("msg", "done"));

	}

	@GetMapping("order-success")
	public String orderSuccess(Model model) {

		model.addAttribute("title", "order success");

		return "User/order_success";
	}

	@GetMapping("/user-orders")
	public String viewOrders(Model model, Principal principal) {

		String name = principal.getName();

		User user = this.userService.getUserByEmail(name);

		List<MyOrder> allOrder = this.myOrderService.getOrderByUserId(user.getId());

		model.addAttribute("orders", allOrder);

		return "User/view_orders";
	}

	@GetMapping("/order-details")
	public String OrderDetails(@RequestParam("orderId") int oid, @RequestParam("productId") int opid, Model model) {

		System.out.println("OID = " + oid);
		System.out.println("PID = " + opid);

		OrderedProduct op = this.orderedProductRepository.findById(opid).orElse(null);
		MyOrder order = this.myOrderService.getOrderById(oid);

//		OrderStatus currentStatusEnum = Arrays.stream(OrderStatus.values())
//				.filter(s -> s.getName().equals(order.getOrderStatus())).findFirst().orElse(OrderStatus.IN_PROGRESS);
//		
//		int currentStatusId = currentStatusEnum.getId();
//		model.addAttribute("currentStatusId", currentStatusId);

		List<OrderStatus> trackingSteps = OrderStatus.getTrackingOrder();
		int currentStep = OrderStatus.getStatusIndex(order.getOrderStatus());

		model.addAttribute("product", op);
		model.addAttribute("order", order);

		model.addAttribute("steps", trackingSteps);
		model.addAttribute("currentStep", currentStep);

		return "User/order_details";

	}

	@GetMapping("/order-address")
	public String orderAddress(Principal principal, Model model) {

		String name = principal.getName();

		User userByEmail = this.userService.getUserByEmail(name);

		model.addAttribute("userId", userByEmail.getId());

		return "User/billing_address";
	}

	@PostMapping("/save-order-address")
	public String saveOrderAddress(@ModelAttribute OrderAddress orderAddress, Principal principal) {

		String name = principal.getName();
		System.out.println("OD = " + orderAddress);

		User user = this.userService.getUserByEmail(name);

		orderAddress.setUser(user);

//		System.out.println(orderAddress);

		this.orderAddressService.saveAddress(orderAddress);

		return "redirect:/user/orders";
	}

	@GetMapping("/deleteAddress")
	public String deleteAddress(@RequestParam("aid") int aid) {

		System.out.println("AID = " + aid);

		this.orderAddressService.deleteAddress(aid);

		return "redirect:/user/orders";
	}

	@PostMapping("/product/{productId}/review")
	public String submitReview(@PathVariable("productId") int pid, @RequestParam("rating") int rating,
			@RequestParam("comment") String comment, Principal p) {
		
		
		User user = this.userService.getUserByEmail(p.getName());
		
		Product product = this.productService.getProductById(pid);
		
		Review review = new Review();
		review.setUser(user);
		review.setProduct(product);
		review.setRating(rating);
		review.setComment(comment);
		
		this.reviewService.saveReview(review);
		
		return "redirect:/product/" + pid;
	}

}
