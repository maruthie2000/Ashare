package com.quantum_paradigm.socialshare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quantum_paradigm.socialshare.dto.TrailUser;
import com.quantum_paradigm.socialshare.dto.User;
import com.quantum_paradigm.socialshare.dto.UserPackages;

public interface UserRepository extends JpaRepository<User, String> {

	User findByVerificationToken(String token);
	List<User> findByEmailOrPhoneNo(String email, long mobile);
	
	User findTopByOrderByUserIdDesc();

	public void save(TrailUser user);
	public void save(UserPackages user);
	
}
