package com.quantum_paradigm.socialshare.dto;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Component
@Data
@Entity
public class User {

	@Id
	@Column(unique = true)
	private String userId;
	private String firstName;
	private String lastName;
	private String email;
	private Long phoneNo;
	private String password;
	private String company;
	private boolean verified;
	private String verificationToken;
	private LocalDate signUpDate;

	@OneToOne(cascade = CascadeType.PERSIST)
	TrailUser trailUser;
	
	@OneToOne
	UserPackages userPackages;

}
