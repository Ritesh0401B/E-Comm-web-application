package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.entities.Category;
import com.ecommerce.entities.MyOrder;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.User;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.MyOrderService;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.UserService;
import com.ecommerce.util.Message;
import com.ecommerce.util.OrderStatus;
import com.ecommerce.util.SessionHelper;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private MyOrderService myOrderService;

	@Autowired
	private SessionHelper sessionHelper;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		if (principal != null) {

			String email = principal.getName();
//			System.out.println("USERNAME " + email);

			User user = this.userService.getUserByEmail(email);
//			System.out.println("USER = " + user);

			model.addAttribute("user", user);
		}

		List<Category> allActiveCategories = this.categoryService.getAllActiveCategories();
		model.addAttribute("categories", allActiveCategories);

	}

	@GetMapping("/")
	public String index(Model model) {

		model.addAttribute("title", "Admin Dashboard");

		sessionHelper.removeMessageFromSession();

		return "Admin/index";

	}

	@GetMapping("/loadAddproduct")
	public String loadAddproduct(Model model) {

		model.addAttribute("title", "Add Product");

		List<Category> allCategory = this.categoryService.getAllCategory();

		model.addAttribute("categories", allCategory);

		return "Admin/add_product";

	}

	// Add Product Modul

	@PostMapping("/add-product")
	public String addProduct(@ModelAttribute Product product, @RequestParam("productImage") MultipartFile file,
			@RequestParam int stock, RedirectAttributes redirectAttributes) {

		System.out.println("PRODUCT = " + product);

		System.out.println("SIZE = " + file.getSize() + " bytes");

		try {

			if (file.isEmpty()) {
				product.setImage("default.png");
			} else {

				System.out.println(file);

				product.setImage(file.getOriginalFilename());

				File file2 = new ClassPathResource("static/images/product").getFile();
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("uploaded..........");

			}

			product.setStock(stock);

			product.setDiscount(0);

			product.setDiscountPrice(product.getPrice());

			Product saveProduct = this.productService.saveProduct(product);

			if (saveProduct != null) {

				redirectAttributes.addFlashAttribute("message",
						new Message("Product Added Successfuly !!", "alert-success"));

			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return "redirect:/admin/loadAddproduct";
	}

	@GetMapping("/view-product")
	public String viewProduct(Model model) {

		model.addAttribute("title", "View Product");

		List<Product> allProducts = this.productService.getAllProduct();

//		System.out.println("All Products" + allProducts);

		model.addAttribute("products", allProducts);

		return "Admin/view_product";
	}

	@GetMapping("/update-product/{id}")
	public String updateProduct(@PathVariable int id, Model model) {

		model.addAttribute("title", "Update Product");

		Product productById = this.productService.getProductById(id);

		model.addAttribute("product", productById);

		List<Category> allCategory = this.categoryService.getAllCategory();

		model.addAttribute("categories", allCategory);

		return "Admin/update_product";
	}

	@PostMapping("/process-update-product")
	public String processUpdateProduct(@ModelAttribute Product product,
			@RequestParam("productImage") MultipartFile file) {

		System.out.println(product.getId());

		System.out.println("PRODUCT = " + product);

		try {

			Product oldProduct = this.productService.getProductById(product.getId());

			if (file.isEmpty()) {

				product.setImage(oldProduct.getImage());

			} else {

				// Delete old photo

				if (oldProduct.getImage() != null && oldProduct.getImage().isBlank()) {

					File deleteFile = new ClassPathResource("static/images/product").getFile();

					File file1 = new File(deleteFile, oldProduct.getImage());

					file1.delete();

				}

				// Update image

				File file2 = new ClassPathResource("static/images/product").getFile();

				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				product.setImage(file.getOriginalFilename());

			}

			if (product.getDiscount() < 0 || product.getDiscount() > 100) {

				product.setDiscount(oldProduct.getDiscount());

			} else {

				product.setDiscount(product.getDiscount());

				Double discountPrice = product.getPrice() - ((product.getPrice() * product.getDiscount()) / 100);
				Double round = (double) Math.round(discountPrice);
				product.setDiscountPrice(round);

			}

			this.productService.saveProduct(product);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/admin/view-product";
	}

	@GetMapping("/delete-product/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {

		boolean deleteProduct = this.productService.deleteProduct(id);

		if (deleteProduct) {

			session.setAttribute("message", new Message("Product Successfuly deleted !!", "alert-success"));

		} else {
			session.setAttribute("message", new Message("Error !!", "alert-danger"));
		}

		return "redirect:/admin/view-product";
	}

	@GetMapping("/category")
	public String category(Model model) {

		model.addAttribute("title", "Add Category");

		List<Category> allCategory = this.categoryService.getAllCategory();

		model.addAttribute("category", allCategory);

		return "Admin/category";

	}

	@PostMapping("/saveCategory")
	public String savecategory(@ModelAttribute Category category, @RequestParam("image") MultipartFile file,
			@RequestParam boolean isActive, HttpSession session) throws IOException {

		System.out.println(category);

		if (file.isEmpty()) {
			category.setImageName("default.png");
		} else {

			category.setImageName(file.getOriginalFilename());

			File file2 = new ClassPathResource("static/images/category").getFile();
			Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			System.out.println("file uploaded........");

		}

		String name = category.getName();
		category.setActive(isActive);

		boolean existCategory = categoryService.existCategory(name);

		if (!existCategory) {

			Category saveCategory = categoryService.saveCategory(category);

			if (saveCategory != null) {
				session.setAttribute("message", new Message("Category Added Successfuly !!", "alert-success"));
			}

			return "redirect:/admin/category";

		} else {

			session.setAttribute("message", new Message("Category Already Exist !!", "alert-warning"));

			return "redirect:/admin/category";

		}

	}

	@GetMapping("/delete-category/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {

		boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {

			session.setAttribute("message", new Message("Category Successfuly deleted !!", "alert-success"));

		} else {
			session.setAttribute("message", new Message("Error !!", "alert-danger"));
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/update-category/{id}")
	public String updateCategory(@PathVariable int id, Model model) {

		model.addAttribute("title", "Update Category");

		Category categoryById = this.categoryService.getCategoryById(id);

		model.addAttribute("category", categoryById);

		return "Admin/update_category";
	}

	@PostMapping("/process-update-category")
	public String processUpdateCategory(@ModelAttribute Category category, @RequestParam boolean isActive,
			HttpSession session, @RequestParam("image") MultipartFile file) throws IOException {

		try {

			Category oldCategory = this.categoryService.getCategoryById(category.getId());

			if (file.isEmpty()) {
				// agar file empty hai to purani photo ko rakhne ke liye
				category.setImageName(oldCategory.getImageName());

			} else {

				// Delete old photo

				if (oldCategory.getImageName() != null && oldCategory.getImageName().isBlank()) {

					File deleteFile = new ClassPathResource("static/images/category").getFile();

					File file1 = new File(deleteFile, oldCategory.getImageName());

					file1.delete();

				}

				// update new photo

				File file2 = new ClassPathResource("static/images/category").getFile();

				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				category.setImageName(file.getOriginalFilename());

			}

			if (!ObjectUtils.isEmpty(category)) {

				category.setName(category.getName());
				category.setActive(isActive);

			}

			Category updateCategory = this.categoryService.saveCategory(category);

			if (ObjectUtils.isEmpty(updateCategory)) {

			}

			return "redirect:/admin/category";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/category";

	}

	@GetMapping("/view-users")
	public String viewUsers(Model model) {

		model.addAttribute("title", "View Users");

		List<User> allUsers = this.userService.getAllUsersByRole();

//		System.out.println("All Products" + allProducts);

		model.addAttribute("users", allUsers);

		return "Admin/view_users";
	}

	@GetMapping("/update-status")
	public String updateUserAcountStatus(@RequestParam Boolean status, @RequestParam int id,
			RedirectAttributes redirectAttributes) {

		boolean f = this.userService.updateAccountStatus(id, status);

		if (f) {
			redirectAttributes.addFlashAttribute("message", new Message("Account Status Updated !!", "text-success"));
		} else {
			redirectAttributes.addFlashAttribute("message", new Message("Account Status Updated !!", "text-danger"));
		}

		return "redirect:/admin/view-users";
	}

	@GetMapping("/delete-user/{id}")
	public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {

		boolean deleteUser = this.userService.deleteUser(id);

		if (deleteUser) {

			redirectAttributes.addFlashAttribute("message", new Message("User Successfuly deleted !!", "text-success"));

		} else {
			redirectAttributes.addFlashAttribute("message", new Message("Error !!", "text-danger"));
		}

		return "redirect:/admin/view-users";
	}

	@GetMapping("view-orders")
	public String getUserOrder(Model model) {

		List<User> users = this.userService.getAllEnabledUsers();
		model.addAttribute("users", users);

		return "Admin/view_order";
	}

	@GetMapping("/view-user-orders")
	public String viewUserOrders(Model model) {
		
		List<MyOrder> allOrders = this.myOrderService.getAllOrders();

		List<OrderStatus> step = OrderStatus.getTrackingOrders();

		model.addAttribute("orders", allOrders);
		model.addAttribute("step", step);

		return "Admin/view_user_orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam("orderId") Integer oid, @RequestParam String status,
			Model model) {

		System.out.println(oid);
		System.out.println(status);

		if (oid != null) {

			MyOrder order = this.myOrderService.getOrderById(oid);
			
			order.setOrderStatus(status);
			
			this.myOrderService.updateOrder(order);

		}

		return "redirect:/admin/view-user-orders";
	}

}
