package com.quantum_paradigm.socialshare.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
import com.quantum_paradigm.socialshare.helper.GenerateUserId;
import com.quantum_paradigm.socialshare.helper.SecurePassword;
import com.quantum_paradigm.socialshare.helper.SendMail;
import com.quantum_paradigm.socialshare.response.ResponseStructure;

@Service
public class UserService {

	@Autowired
	UserDao userDao;
	
	@Autowired
	SecurePassword securePassword;
	
	@Autowired
	SendMail sendMail;
	
	@Autowired
	User user;
	
	@Autowired
	GenerateUserId generateUserId;
	
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
		List<User> exUser = userDao.findByEmailOrMobile(u.getEmail(),u.getPhoneNo());
		if (!exUser.isEmpty()) {
			structure.setMessage("Account Already exist");
			structure.setCode(HttpStatus.NOT_ACCEPTABLE.value());
			structure.setStatus("error");
//			structure.setRecommendedHashtag(null);
//			structure.setHashtag(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_ACCEPTABLE);
		} else {
			
//			userDao.saveUser(u);
			
			 String userId = generateUserId.generateuserId();
	            u.setUserId(userId);
	            u.setPassword(securePassword.encrypt(u.getPassword(), "123"));
	            userDao.saveUser(u);

	            String verificationToken = UUID.randomUUID().toString();
	            u.setVerificationToken(verificationToken);
	            userDao.insert(u);

	            sendMail.sendVerificationEmail(u); 
	            
			structure.setCode(HttpStatus.CREATED.value());
			structure.setStatus("success");
			structure.setMessage("successfully signedup");
			structure.setData(u);
			structure.setHashtag(null);
			structure.setRecommendedHashtag(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.CREATED);
		}
	}

//	public ResponseEntity<ResponseStructure<String>> userLogin(String emailOrPhone, String password,
//			HttpSession httpSession) {
//		long phone = 0;
//		String email = null;
//		try {
//			phone = Long.parseLong(emailOrPhone);
//		} catch (NumberFormatException e) {
//			email = emailOrPhone;
//		}
//		List<User> exUser = userDao.findUserByEmailOrPhoneNo(email, phone);
//		if (exUser.isEmpty()) {
//			structure.setCode(HttpStatus.NOT_FOUND.value());
//			structure.setMessage("Invalid email or phone number");
//			structure.setStatus("error");
//			structure.setRecommendedHashtag(null);
//			structure.setHashtag(null);
//			structure.setData(null);
//			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
//		} else {
//			User signedUpUser = exUser.get(0);
//			if (signedUpUser.getPassword().equals(password)) {
//				structure.setCode(HttpStatus.OK.value());
//				structure.setStatus("success");
//				structure.setData(exUser.toString());
//				structure.setMessage("Login Successful");
//				structure.setRecommendedHashtag(null);
//				structure.setHashtag(null);
//				return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
//
//			} else {
//				structure.setCode(HttpStatus.BAD_REQUEST.value());
//				structure.setStatus("error");
//				structure.setData(exUser.toString());
//				structure.setMessage("Invalid password");
//				structure.setRecommendedHashtag(null);
//				structure.setData(null);
//				structure.setHashtag(null);
//				return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_FOUND);
//
//			}
//		}
//	}
	public ResponseEntity<String> verifyEmail(String token) {
        User user = userDao.findByVerificationToken(token);
        if (user != null) {
            user.setVerified(true);
//            userDao.update(user);
            userDao.saveUser(user);
            user.setSignUpDate(LocalDate.now());
    		trailUser.setTrial(true);
    		trailUser.setTrialStartDate(LocalDate.now());
    		userDao.saveTrailUser(trailUser);
    		user.setTrailUser(trailUser);
    		userDao.saveUser(user);
//            return ResponseEntity.ok("Email verification successful! Redirecting to login page...");
//            structure.setCode(HttpStatus.CREATED.value());
//			structure.setStatus("Email verification successful! Redirecting to login page...");
//			structure.setMessage("successfully signedup");
//			structure.setData(user);
//			structure.setHashtag(null);
//			structure.setRecommendedHashtag(null);
			//return new ResponseEntity<ResponseStructure<String>>(HttpStatus.CREATED);
    		 return ResponseEntity.ok("Email verification successful! Redirecting to login page...");
        } else {
//        	structure.setCode(HttpStatus.NOT_FOUND.value());
//			structure.setMessage("Please ");
//			structure.setStatus("Email verification failed...");
//			structure.setRecommendedHashtag(null);
//			structure.setHashtag(null);
//			structure.setData(null);
        	 return ResponseEntity.ok("Email verification Failed! Redirecting to login page...");
        }
    }
	
	public ResponseEntity<String> login(String emph, String password) {
        long mobile = 0;
        String email = null;
        try {
            mobile = Long.parseLong(emph);
        } catch (NumberFormatException e) {
            email = emph;
        }
        List<User> users = userDao.findByEmailOrMobile(email, mobile);
        if (users.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid email or mobile");
        } else {
            User user = users.get(0);
            if (SecurePassword.decrypt(user.getPassword(), "123").equals(password)) {
                if (user.isVerified()) {
                    return ResponseEntity.ok("Login successful");
                } else {
                    String verificationToken = UUID.randomUUID().toString();
                    user.setVerificationToken(verificationToken);
                    userDao.insert(user);
                    sendMail.sendVerificationEmail(user);
                    return ResponseEntity.ok("Please verify your email");
                }
            } else {
                return ResponseEntity.badRequest().body("Invalid password");
            }
        }
    }
	
//	public ResponseEntity<ResponseStructure<String>> verifyUser(User u) {
//		User user=userDao.fetchUser(u.getUid());
//		
//		user.setVerified(true);
//		user.setSignUpDate(LocalDate.now());
//		trailUser.setTrial(true);
//		trailUser.setTrialStartDate(LocalDate.now());
//		userDao.saveTrailUser(trailUser);
//		user.setTrailUser(trailUser);
//		userDao.saveUser(user);
//		
//		structure.setCode(HttpStatus.OK.value());
//		structure.setMessage("Verified SuccessFully");
//		structure.setStatus("success");
//		structure.setData(null);
//		structure.setHashtag(null);
//		structure.setRecommendedHashtag(null);
//		return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);
//	}
}
