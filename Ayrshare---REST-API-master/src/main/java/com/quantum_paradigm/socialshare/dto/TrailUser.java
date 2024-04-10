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
public class TrailUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int trailId;
	private boolean trial;
	private LocalDate trialStartDate;

}
