package com.ecommerce.services;

import java.util.List;

import com.ecommerce.entities.User;

public interface UserService {
	
	public User getUserByEmail(String email);
	
	public User saveUser(User user);
	
	public List<User> getAllUsers();
	
	public List<User> getAllUsersByRole();
	
	public boolean deleteUser(int id);

	public boolean updateAccountStatus(int id, Boolean status);
	
	public void increaseFailedAttempt(User user);
	
	public void userAccountLock(User user);
	
	public boolean unlockAccountTimeExpired(User user);
	
	public void resetAttempt(int id);

	public void updateUserResetToken(String email, String resetToken);

	public User getUserByToken(String token);

	public User getUserById(int uid);

	public List<User> getAllEnabledUsers();
	
}
