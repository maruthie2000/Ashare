package com.quantum_paradigm.socialshare.dto;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int uid;
	private String userId;
	private String firstName;
	private String lastName;
	private String email;
	private Long phoneNo;
	private String password;
	private String company;
	private boolean verified;
	private LocalDate signUpDate;
	
	@OneToOne
	TrailUser trailUser;
	
	@OneToOne
	UserPackages userPackages;

}
