package com.quantum_paradigm.socialshare.dto;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@Component
public class UserPackages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pid;
	private LocalDate subscriptionDate;
	private boolean isSubscribed;
	private String packageId;
	private LocalDate packageExpire;
	private String packageAmount;
	private String packageName;
	private int subscriptionDays;

}
