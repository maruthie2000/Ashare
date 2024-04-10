package com.quantum_paradigm.socialshare.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quantum_paradigm.socialshare.dto.UserPackages;
import com.quantum_paradigm.socialshare.repository.UserPackageRepository;

@Component
public class UserPackageDao {

	@Autowired
	UserPackageRepository packageRepository;

	public void savePackage(UserPackages packages) {
		packageRepository.save(packages);
	}

}
