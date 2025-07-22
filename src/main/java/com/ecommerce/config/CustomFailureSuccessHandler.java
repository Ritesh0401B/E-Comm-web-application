package com.ecommerce.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecommerce.dao.UserRepository;
import com.ecommerce.entities.User;
import com.ecommerce.services.UserService;
import com.ecommerce.services.impl.UserServiceImpl;
import com.ecommerce.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomFailureSuccessHandler extends SimpleUrlAuthenticationFailureHandler {

	private final UserServiceImpl userServiceImpl;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	CustomFailureSuccessHandler(UserServiceImpl userServiceImpl) {
		this.userServiceImpl = userServiceImpl;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String email = request.getParameter("username");

		User user = this.userRepository.getUserByUserName(email);

		if (user != null) {

			if (user.isEnable()) {

				if (user.isAccountNonLocked()) {

					if (user.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {

						userService.increaseFailedAttempt(user);

					} else {
						
						userService.userAccountLock(user);
						exception = new LockedException("your account is locked !! failed attempt 3");

					}

				} else {
					
					if(userService.unlockAccountTimeExpired(user)){
						
						exception = new LockedException("your account is unlocked !! please try to login");
						
					}else {
						exception = new LockedException("your account is locked !! please try after sometimes");
					}
					
				}

			} else {
				exception = new LockedException("Your account is inactive");
			}

		} else {
			exception = new LockedException("Email & password invalid");
		}

		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}
