package com.ecommerce.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.email = :email")
	public User getUserByUserName(@Param("email") String email);

	@Query("select u from User u where u.role = 'ROLE_USER'")
	public List<User> findByRole();

	public User findByResetToken(String token);

	@Query("select u from User u where u.role = 'ROLE_USER' AND u.isEnable = true ")
	public List<User> findAllEnabledUsers();

}
