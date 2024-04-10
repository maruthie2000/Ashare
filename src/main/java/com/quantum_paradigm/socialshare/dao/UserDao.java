package com.quantum_paradigm.socialshare.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quantum_paradigm.socialshare.dto.TrailUser;
import com.quantum_paradigm.socialshare.dto.User;
import com.quantum_paradigm.socialshare.dto.UserPackages;
import com.quantum_paradigm.socialshare.repository.UserRepository;

@Component
public class UserDao {
	@Autowired
	UserRepository uRepository;

	public void saveUser(User u) {
		uRepository.save(u);
	}

//	public List<User> findUserByEmail(String email) {
//		return uRepository.findByEmail(email);
//	}

	public List<User> findByEmailOrMobile(String email, long mobile) {
		return uRepository.findByEmailOrPhoneNo(email, mobile);
	}

	public void insert(User userDto) {
		uRepository.save(userDto);
	}

	public User findById(String id) {
		return uRepository.findById(id).orElseThrow(null);

	}

	public void update(User user) {
		uRepository.save(user);

	}

	public User findByVerificationToken(String token) {
		return uRepository.findByVerificationToken(token);
	}

	public String findLastUserId() {
		User latestUser = uRepository.findTopByOrderByUserIdDesc();
		if (latestUser != null) {
			return latestUser.getUserId();
		}
		return null;
	}

	public void saveTrailUser(TrailUser user) {
		uRepository.save(user);
	}

	public void saveSubcribedUser(UserPackages subcribedUser) {
		uRepository.save(subcribedUser);

	}

	public User fetchUser(String id) {
		return uRepository.findById(id).orElse(null);
	}
}
