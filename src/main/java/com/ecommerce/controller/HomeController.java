package com.ecommerce.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import com.ecommerce.dao.UserRepository;
import com.ecommerce.entities.Category;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.User;
import com.ecommerce.services.CartService;
import com.ecommerce.services.CategoryService;
import com.ecommerce.services.ProductService;
import com.ecommerce.services.UserService;
import com.ecommerce.util.CommonUtil;
import com.ecommerce.util.Message;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class HomeController /* ~~(Could not parse as Java)~~> */ {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CartService cartService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CommonUtil commonUtil;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		if (principal != null) {

			String email = principal.getName();
//			System.out.println("USERNAME " + email);

			User user = this.userService.getUserByEmail(email);
//			System.out.println("USER = " + user);

			model.addAttribute("user", user);

			Integer countCart = this.cartService.getCountCart(user.getId());

			model.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategories = this.categoryService.getAllActiveCategories();
		model.addAttribute("categories", allActiveCategories);

	}

	@GetMapping("/home")
	public String home(Model model) {

		model.addAttribute("title", "Home Page");

		List<Category> allCategory = this.categoryService.getAllCategory();
		
		List<Product> allProduct = this.productService.getAllProduct();

		model.addAttribute("category", allCategory);
		model.addAttribute("products", allProduct);

		return "home";
	}

	@GetMapping("/signin")
	public String login(Model model) {

		model.addAttribute("title", "Login");

		return "login";

	}

	// Forgot Password Module

	@GetMapping("/forgot")
	public String forgotPassword(Model model) {

		model.addAttribute("title", "Forgot Password");

		return "forgot_password";
	}

	@PostMapping("/process-forgot-password")
	public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {

		User userByEmail = this.userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			redirectAttributes.addFlashAttribute("message", new Message("Invalid email", "text-success"));
		} else {

			String resetToken = UUID.randomUUID().toString();
			this.userService.updateUserResetToken(email, resetToken);

			// Generate URL :
			// http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendEmail = commonUtil.sendEmail(url, email);

			if (sendEmail) {
				redirectAttributes.addFlashAttribute("message",
						new Message("Please check your email. Password reset link sent", "text-success"));

			} else {
				redirectAttributes.addFlashAttribute("message",
						new Message("Something wrong on server ! Email not send", "text-danger"));
			}

		}

		return "redirect:/forgot";
	}

	@GetMapping("/reset-password")
	public String resetPassword(Model model, @RequestParam String token, RedirectAttributes redirectAttributes) {

		model.addAttribute("title", "Reset Password");

		System.out.println(token);

		User userByToken = this.userService.getUserByToken(token);

		if (userByToken == null) {
			redirectAttributes.addFlashAttribute("message",
					new Message("Your link is invalid or expired !!", "text-danger"));

			return "redirect:/forgot";

		}

		model.addAttribute("token", token);

		return "reset_password";
	}

	@PostMapping("/process-reset-password")
	public String processResetPassword(@RequestParam String token, @RequestParam("newPassword") String newPassword,
			Model model, @RequestParam("confirmPassword") String confirmPassword,
			RedirectAttributes redirectAttributes) {

		User userByToken = this.userService.getUserByToken(token);

		if (userByToken == null) {
			redirectAttributes.addFlashAttribute("message",
					new Message("Your link is invalid or expired !!", "text-danger"));

			return "redirect:/forgot";

		} else {

			if (!newPassword.equals(confirmPassword)) {
				redirectAttributes.addFlashAttribute("message",
						new Message("New password and confirm password do not match!", "text-danger"));

				return "redirect:/reset-password?token=" + token;
			}

			userByToken.setPassword(passwordEncoder.encode(newPassword));
			userByToken.setResetToken(null);

			this.userService.saveUser(userByToken);

			redirectAttributes.addFlashAttribute("message",
					new Message("Password changed successfully!", "text-success"));

		}

		return "redirect:/signin?change=Password changed successfully!!";
	}

	@GetMapping("/register")
	public String register(Model model) {

		model.addAttribute("title", "Register");

		return "register";

	}

	@PostMapping("/process-register")
	public String processRegisret(@ModelAttribute User user, @RequestParam("image") MultipartFile file) {

		System.out.println("USER = " + user);
		System.out.println("Image = " + file.getOriginalFilename());

		try {

			String imageName = file.isEmpty() ? "profile.png" : file.getOriginalFilename();

			user.setProfileImage(imageName);
			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setEnable(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User saveUser = this.userService.saveUser(user);

			if (!ObjectUtils.isEmpty(saveUser)) {

				if (!file.isEmpty()) {

					System.out.println(file);

					File file2 = new ClassPathResource("static/images/user_profile").getFile();
					Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

					System.out.println("uploaded..........");

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/signin";
	}

	@GetMapping("/products")
	public String products(Model model, @RequestParam(value = "category", defaultValue = "") String category) {

		model.addAttribute("title", "Products");
		
		List<Category> allActiveCategories = this.categoryService.getAllActiveCategories();

		model.addAttribute("categories", allActiveCategories);

		List<Product> allActiveProduct = this.productService.getAllActiveProduct(category);

		model.addAttribute("products", allActiveProduct);

		model.addAttribute("paramValue", category);

		return "product";

	}

	@GetMapping("/product/{id}")
	public String viewProducts(@PathVariable Integer id, Model model) {

		model.addAttribute("title", "Product");

		Product productById = this.productService.getProductById(id);

		model.addAttribute("product", productById);

		return "view_product";

	}

}
