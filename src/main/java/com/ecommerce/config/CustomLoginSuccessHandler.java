package com.ecommerce.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// getb the role
		
		String role = authentication.getAuthorities().iterator().next().getAuthority();
		
		if(role.equals("ROLE_ADMIN")) {
			response.sendRedirect("/admin/");
		}else if(role.equals("ROLE_USER")) {
			response.sendRedirect("/home");
		}else {
			response.sendRedirect("/signin?error=true");
		}
		
	}

}
