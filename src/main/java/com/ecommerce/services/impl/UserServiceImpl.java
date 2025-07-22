package com.ecommerce.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.dao.UserRepository;
import com.ecommerce.entities.User;
import com.ecommerce.services.UserService;
import com.ecommerce.util.AppConstant;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User saveUser(User user) {

		User user1 = this.userRepository.save(user);

		return user1;
	}
	
	@Override
	public List<User> getAllUsers() {
		
		List<User> users = this.userRepository.findAll();
		
		return users;
		
	}
	
	@Override
	public User getUserById(int uid) {
		
		User user = this.userRepository.findById(uid).orElse(null);
		
		return user;
	}


	@Override
	public User getUserByEmail(String email) {
		User userByUserName = this.userRepository.getUserByUserName(email);

		return userByUserName;
	}

	@Override
	public List<User> getAllEnabledUsers() {
		
		List<User> users = this.userRepository.findAllEnabledUsers();
		
		return users;
	}
	
	@Override
	public List<User> getAllUsersByRole() {
		
		List<User> user = this.userRepository.findByRole();
		
		return user;
	}


	@Override
	public boolean deleteUser(int id) {
		
		User user = this.userRepository.findById(id).orElse(null);
		
		if(!ObjectUtils.isEmpty(user)) {
			
			this.userRepository.delete(user);
			
			return true;
			
		}
		
		return false;
	}

	@Override
	public boolean updateAccountStatus(int id, Boolean status) {
		
		Optional<User> findById = this.userRepository.findById(id);
				
		if(findById.isPresent()) {
			
			User user = findById.get();
			
			user.setEnable(status);
			
			this.userRepository.save(user);
			
			return true;
			
		}
		
		return false;
	}

	@Override
	public void increaseFailedAttempt(User user) {
		
		int attempt = user.getFailedAttempt() + 1;
		
		user.setFailedAttempt(attempt);
		
		this.userRepository.save(user);
		
	}

	@Override
	public void userAccountLock(User user) {
		
		user.setAccountNonLocked(false);
		
		user.setLockTime(new Date());
		
		this.userRepository.save(user);
		
	}

	@Override
	public boolean unlockAccountTimeExpired(User user) {
		
		long lockTime = user.getLockTime().getTime();
		long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		
		long currentTime = System.currentTimeMillis();
		
		if(unlockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			
			this.userRepository.save(user);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void resetAttempt(int id) {
		
		
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		
		User userByUserName = this.userRepository.getUserByUserName(email);
		
		userByUserName.setResetToken(resetToken);
		
		this.userRepository.save(userByUserName);
		
	}

	@Override
	public User getUserByToken(String token) {
		
		User byToken = this.userRepository.findByResetToken(token);
		
		return byToken;
	}

	
	
}
