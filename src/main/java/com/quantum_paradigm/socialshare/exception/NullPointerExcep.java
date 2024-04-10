package com.quantum_paradigm.socialshare.exception;

public class NullPointerExcep extends RuntimeException {
	String message;

	public NullPointerExcep(String message) {
		this.message = message;
	}
}
