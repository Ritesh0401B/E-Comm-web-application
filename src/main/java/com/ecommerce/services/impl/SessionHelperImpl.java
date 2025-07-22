package com.ecommerce.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ecommerce.util.SessionHelper;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionHelperImpl implements SessionHelper {

	@Override
	public void removeMessageFromSession() {
		
		try {
			
			System.out.println("Removing message from session");
			HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			
			session.removeAttribute("message");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	

}
