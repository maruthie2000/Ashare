package com.quantum_paradigm.socialshare.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.quantum_paradigm.socialshare.dao.UserDao;
import com.quantum_paradigm.socialshare.dao.UserPackageDao;
import com.quantum_paradigm.socialshare.dto.TrailUser;
import com.quantum_paradigm.socialshare.dto.User;
import com.quantum_paradigm.socialshare.dto.UserPackages;
import com.quantum_paradigm.socialshare.response.ResponseStructure;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	ResponseStructure<String> structure;

	@Autowired
	UserPackages userPackages;

	@Autowired
	UserPackageDao packageDao;
	
	@Autowired
	TrailUser trailUser;

	@Value("${quantum.freeTrail}")
	int freeTrail;

	public ResponseEntity<ResponseStructure<String>> userSignUp(User u) {
		List<User> exUser = userDao.findUserByEmail(u.getEmail());
		if (!exUser.isEmpty()) {
			structure.setMessage("Account ALready exist");
			structure.setCode(HttpStatus.NOT_ACCEPTABLE.value());
			structure.setStatus("error");
			structure.setRecommendedHashtag(null);
			structure.setHashtag(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_ACCEPTABLE);
		} else {
			userDao.saveUser(u);

			structure.setCode(HttpStatus.CREATED.value());
			structure.setStatus("success");
			structure.setMessage("successfully signedup");
			structure.setData(u);
			structure.setHashtag(null);
			structure.setRecommendedHashtag(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.CREATED);
		}
	}

	public ResponseEntity<ResponseStructure<String>> userLogin(String emailOrPhone, String password,
			HttpSession httpSession) {
		long phone = 0;
		String email = null;
		try {
			phone = Long.parseLong(emailOrPhone);
		} catch (NumberFormatException e) {
			email = emailOrPhone;
		}
		List<User> exUser = userDao.findUserByEmailOrPhoneNo(email, phone);
		if (exUser.isEmpty()) {
			structure.setCode(HttpStatus.NOT_FOUND.value());
			structure.setMessage("Invalid email or phone number");
			structure.setStatus("error");
			structure.setRecommendedHashtag(null);
			structure.setHashtag(null);
			structure.setData(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
		} else {
			User signedUpUser = exUser.get(0);
			if (signedUpUser.getPassword().equals(password)) {
				structure.setCode(HttpStatus.OK.value());
				structure.setStatus("success");
				structure.setData(exUser.toString());
				structure.setMessage("Login Successful");
				structure.setRecommendedHashtag(null);
				structure.setHashtag(null);
				return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);

			} else {
				structure.setCode(HttpStatus.BAD_REQUEST.value());
				structure.setStatus("error");
				structure.setData(exUser.toString());
				structure.setMessage("Invalid password");
				structure.setRecommendedHashtag(null);
				structure.setData(null);
				structure.setHashtag(null);
				return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);

			}
		}
	}

	public ResponseEntity<ResponseStructure<String>> verifyUser(User u) {
		User user=userDao.fetchUser(u.getUid());
		
		user.setVerified(true);
		user.setSignUpDate(LocalDate.now());
		trailUser.setTrial(true);
		trailUser.setTrialStartDate(LocalDate.now());
		userDao.saveTrailUser(trailUser);
		user.setTrailUser(trailUser);
		userDao.saveUser(user);
		
		structure.setCode(HttpStatus.OK.value());
		structure.setMessage("Verified SuccessFully");
		structure.setStatus("success");
		structure.setData(null);
		structure.setHashtag(null);
		structure.setRecommendedHashtag(null);
		return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);
	}
}
