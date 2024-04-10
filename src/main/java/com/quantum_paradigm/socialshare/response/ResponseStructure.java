package com.quantum_paradigm.socialshare.response;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStructure<T> {
	String message;
	String status;
	int code;
	Object data;
	List<String> hashtag;
	Map<String, Long> recommendedHashtag;

}
