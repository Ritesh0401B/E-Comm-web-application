package com.ecommerce.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.entities.User;


public class CustomUserDetails implements UserDetails {
	
	private User user;
	

	public CustomUserDetails(User user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole());
		
		return List.of(simpleGrantedAuthority);
	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();  // Email se login ho raha hai
	}

	
	@Override
    public boolean isAccountNonExpired() {
        return true;
    }
	
	@Override
	public boolean isAccountNonLocked() {
		
		return user.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return user.isEnable();
	}
	
	
}
