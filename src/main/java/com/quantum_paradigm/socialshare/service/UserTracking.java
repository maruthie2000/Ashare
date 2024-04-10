package com.quantum_paradigm.socialshare.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.quantum_paradigm.socialshare.dao.UserDao;
import com.quantum_paradigm.socialshare.dto.TrailUser;
import com.quantum_paradigm.socialshare.dto.User;
import com.quantum_paradigm.socialshare.dto.UserPackages;
import com.quantum_paradigm.socialshare.response.PackageResponse;

@Service
public class UserTracking {

	@Autowired
	UserDao userDao;

	@Value("${quantum.freeTrail}")
	int freeTrail;

	@Autowired
	PackageResponse packageResponse;

	public ResponseEntity<PackageResponse> calculateRemainingPackageDays(User u) {
		User user = userDao.fetchUser(u.getUserId());
		LocalDate localDate = LocalDate.now();
		int remainingDays = 0;
		if (user.getTrailUser().isTrial()) {
			LocalDate trailDate = user.getTrailUser().getTrialStartDate();
			if ((freeTrail - ChronoUnit.DAYS.between(trailDate, localDate)) > 0) {
				remainingDays = (int) (freeTrail - ChronoUnit.DAYS.between(trailDate, localDate));
				packageResponse.setCode(HttpStatus.OK.value());
				packageResponse.setMessage("remaining access in days : " + remainingDays);
				packageResponse.setRemainingDays(remainingDays);
				packageResponse.setStatus("success");
				return new ResponseEntity<PackageResponse>(packageResponse, HttpStatus.FOUND);
			} else {
				remainingDays = 0;
				TrailUser trailUser = user.getTrailUser();
				trailUser.setTrial(false);
				userDao.saveTrailUser(trailUser);
				user.setTrailUser(trailUser);
				userDao.saveUser(user);

				packageResponse.setCode(HttpStatus.NOT_EXTENDED.value());
				packageResponse.setMessage("remaining access in days : " + remainingDays);
				packageResponse.setRemainingDays(remainingDays);
				packageResponse.setStatus("error");
				return new ResponseEntity<PackageResponse>(packageResponse, HttpStatus.NOT_EXTENDED);
				// set remainingDays to 0
				// make Trial is false
			}
		} else if (user.getUserPackages() != null && user.getUserPackages().isSubscribed()) {
			LocalDate subscriptionDate = user.getUserPackages().getSubscriptionDate();
			int subcriptionDays = user.getUserPackages().getSubscriptionDays();
			if ((subcriptionDays - ChronoUnit.DAYS.between(subscriptionDate, localDate)) > 0) {
				remainingDays = (int) (subcriptionDays - ChronoUnit.DAYS.between(subscriptionDate, localDate));
				packageResponse.setCode(HttpStatus.OK.value());
				packageResponse.setMessage("remaining access in days : " + remainingDays);
				packageResponse.setRemainingDays(remainingDays);
				packageResponse.setStatus("success");
				return new ResponseEntity<PackageResponse>(packageResponse, HttpStatus.FOUND);
			} else {
				remainingDays = 0;
				UserPackages subcribedUser = user.getUserPackages();
				subcribedUser.setSubscribed(false);
				subcribedUser.setSubscriptionDays(0);
				userDao.saveSubcribedUser(subcribedUser);
				user.setUserPackages(subcribedUser);
				userDao.saveUser(user);

				packageResponse.setCode(HttpStatus.NOT_EXTENDED.value());
				packageResponse.setMessage("remaining access in days : " + remainingDays);
				packageResponse.setRemainingDays(remainingDays);
				packageResponse.setStatus("error");
				return new ResponseEntity<PackageResponse>(packageResponse, HttpStatus.NOT_EXTENDED);
				// package expried
			}
		} else {
			packageResponse.setCode(HttpStatus.NOT_FOUND.value());
			packageResponse.setMessage("Package has been expired!! Please Subscribe Your package.");
			packageResponse.setRemainingDays(remainingDays);
			packageResponse.setStatus("error");
			return new ResponseEntity<PackageResponse>(packageResponse, HttpStatus.NOT_FOUND);
		}
	}

}
