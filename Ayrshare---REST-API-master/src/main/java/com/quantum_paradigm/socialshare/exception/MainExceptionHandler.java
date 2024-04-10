package com.quantum_paradigm.socialshare.exception;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.quantum_paradigm.socialshare.response.ResponseStructure;

@ControllerAdvice
public class MainExceptionHandler {

	@ExceptionHandler(ClientException.class)
	public ResponseEntity<ResponseStructure<String>> handle(ClientException exception)
			throws JsonMappingException, JsonProcessingException {
		ResponseStructure<String> structure = new ResponseStructure<>();
		System.out.println("****************" + exception);
		structure.setMessage("Bad Request");
		structure.setCode(HttpStatus.BAD_REQUEST.value());
		structure.setStatus("error");
		String[] split = exception.toString().split(":");
		JSONParser jsonParser = new JSONParser(split[1]);
		System.out.println(jsonParser.toString());

		try {
			String m = exception.getMessage();
			String messageValue = extractMessageValue(m);
			structure.setData(messageValue);
		} catch (Exception e) {
			structure.setData(e.getMessage());
		}

		return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
	}

	private static String extractMessageValue(String jsonData) {
		// Find the index of the substring "message\":\""
		int startIndex = jsonData.indexOf("message\":\"");
		if (startIndex != -1) {
			// Extract the substring after "message\":\""
			String remainingString = jsonData.substring(startIndex + "message\":\"".length());
			// Find the index of the closing quote
			int endIndex = remainingString.indexOf(".");
			if (endIndex != -1) {
				// Extract the message value
				return remainingString.substring(0, endIndex);
			}
		}
		return null;
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ResponseStructure<String>> handleBadRequest(BadRequestException exception) {
		ResponseStructure<String> structure = new ResponseStructure<String>();
		structure.setMessage(exception.getMessage());
		structure.setCode(HttpStatus.NOT_ACCEPTABLE.value());
		structure.setStatus("error");

		structure.setData(exception.getMessage());

		return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ResponseStructure<String>> handleNull(NullPointerException exception) {
		ResponseStructure<String> structure = new ResponseStructure<String>();
		structure.setMessage(exception.getMessage());
		structure.setCode(HttpStatus.BAD_REQUEST.value());
		structure.setStatus("error");

		structure.setData("Bad Request");

		return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(CommonException.class)
	public ResponseEntity<ResponseStructure<String>> handleJsonMapping(CommonException exception) {
		ResponseStructure<String> structure = new ResponseStructure<String>();
		structure.setMessage(exception.getMessage());
		structure.setCode(HttpStatus.NOT_ACCEPTABLE.value());
		structure.setStatus("error");

		structure.setData(exception.getMessage());

		return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.NOT_ACCEPTABLE);
	}
}
