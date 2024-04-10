package com.quantum_paradigm.socialshare.payment;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.quantum_paradigm.socialshare.configuration.ConfigurationClass;
import com.quantum_paradigm.socialshare.dao.UserDao;
import com.quantum_paradigm.socialshare.dao.UserPackageDao;
import com.quantum_paradigm.socialshare.dto.TrailUser;
import com.quantum_paradigm.socialshare.dto.User;
import com.quantum_paradigm.socialshare.dto.UserPackages;
import com.quantum_paradigm.socialshare.response.PackageResponse;
import com.quantum_paradigm.socialshare.response.ResponseStructure;

@Component
public class UserPayment {

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

	@Autowired
	PackageResponse packageResponse;

	@Autowired
	ConfigurationClass configuration;

	public ResponseEntity<PackageResponse> buySubricption(User u, int newSubricptionDays) {

		LocalDate localDate = LocalDate.now();
		User user = userDao.fetchUser(u.getUid());
		UserPackages userPackage = user.getUserPackages();

		if (userPackage != null && userPackage.isSubscribed()) {
			System.out.println("if");
			int oldSubricptionDays = userPackage.getSubscriptionDays();
			oldSubricptionDays = (int) (oldSubricptionDays
					- Math.abs(ChronoUnit.DAYS.between(localDate, user.getUserPackages().getSubscriptionDate())));
			int totalSubscriptionDays = oldSubricptionDays + newSubricptionDays;//
			userPackage.setSubscriptionDate(localDate);
			userPackage.setSubscriptionDays(totalSubscriptionDays);
			userPackage.setPackageAmount("100");
			userPackage.setPackageExpire(localDate.plusDays(totalSubscriptionDays));
			userPackage.setPackageId("trail");
			userPackage.setPackageName("premium package");
			userPackage.setSubscribed(true);

			packageDao.savePackage(userPackage);
			user.setUserPackages(userPackage);
			userDao.saveUser(user);

			packageResponse.setCode(HttpStatus.OK.value());
			packageResponse.setMessage("remaining access in days : " + totalSubscriptionDays);
			packageResponse.setRemainingDays(totalSubscriptionDays);
			packageResponse.setStatus("success");
			return new ResponseEntity<PackageResponse>(packageResponse, HttpStatus.ACCEPTED);
			// set this new userDate to database
		} else {
			if(user.getTrailUser().isTrial()) {
				TrailUser trailUser=user.getTrailUser();
				trailUser.setTrial(false);
				userDao.saveTrailUser(trailUser);
			}
			
			System.out.println("else");
			UserPackages packages = configuration.getUSerPackage();
			packages.setSubscriptionDate(localDate);
			packages.setSubscriptionDays(newSubricptionDays);
			packages.setPackageAmount("100");
			packages.setPackageExpire(localDate.plusDays(newSubricptionDays));
			packages.setPackageId("trail");
			packages.setPackageName("premium package");
			packages.setSubscribed(true);

			packageDao.savePackage(packages);
			user.setUserPackages(packages);
			userDao.saveUser(user);

			packageResponse.setCode(HttpStatus.OK.value());
			packageResponse.setMessage("remaining access in days : " + newSubricptionDays);
			packageResponse.setRemainingDays(newSubricptionDays);
			packageResponse.setStatus("success");
			return new ResponseEntity<PackageResponse>(packageResponse, HttpStatus.ACCEPTED);
			// set the new subcription date for user because is package has been expried

		}

	}
}
