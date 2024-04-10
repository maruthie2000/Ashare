package com.quantum_paradigm.socialshare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quantum_paradigm.socialshare.dto.User;
import com.quantum_paradigm.socialshare.payment.UserPayment;
import com.quantum_paradigm.socialshare.response.PackageResponse;
import com.quantum_paradigm.socialshare.response.ResponseStructure;
import com.quantum_paradigm.socialshare.service.UserService;
import com.quantum_paradigm.socialshare.service.UserTracking;

@RestController
@RequestMapping("/quantum-socialshare/user")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserTracking userTracking;
	
	@Autowired
	UserPayment userpayment;

	@PostMapping("/login")
	public ResponseEntity<String> userLogin(@RequestParam String emph, @RequestParam String password) {
		
		return userService.login(emph, password);
	}

//	@PostMapping("/signUp")
//	public ResponseEntity<ResponseStructure<String>> userSignUp(@RequestBody User user) {
//		System.out.println(user);
//		return userService.userSignUp(user);
//	}
	@PostMapping("/signup") 
    public ResponseEntity<ResponseStructure<String>> signup(@RequestBody User userDto) {
        return userService.userSignUp(userDto);
    }

	@GetMapping("/access/remainingdays")
	public ResponseEntity<PackageResponse> userRemainingDays(@RequestBody User user) {
		System.out.println(user);
		return userTracking.calculateRemainingPackageDays(user);
	}

	@GetMapping("/verify")
	public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
		return userService.verifyEmail(token);
	}

	@PostMapping("/buy/subscription")
	public ResponseEntity<PackageResponse> buySubscription(@RequestBody User user) {
		return userpayment.buySubricption(user,30);
	}
}
