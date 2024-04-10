package com.quantum_paradigm.socialshare.response;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageResponse {
	String message;
	String status;
	int code;
	int remainingDays;
}
