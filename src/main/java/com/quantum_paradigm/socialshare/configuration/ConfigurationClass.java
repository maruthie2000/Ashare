package com.quantum_paradigm.socialshare.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.quantum_paradigm.socialshare.dto.UserPackages;
import com.quantum_paradigm.socialshare.response.ErrorResponse;
import com.quantum_paradigm.socialshare.response.PostResponse;
import com.quantum_paradigm.socialshare.response.SuccessResponse;

@Component
public class ConfigurationClass {

	@Bean
	public HttpHeaders HttpHeaders() {
		return new HttpHeaders();
	}

	@Bean
	@Lazy
	public HttpEntity<String> getHttpEntity(String jsonString, HttpHeaders headers) {
		return new HttpEntity<>(jsonString, headers);
	}

	@Bean
	@Lazy
	public HttpEntity<String> getHttpEntity(HttpHeaders headers) {
		return new HttpEntity<>(headers);
	}

	@Bean
	@Lazy
	public Map<String, Long> getMap() {
		return new HashMap<String, Long>();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public List<PostResponse> responseList() {
		return new ArrayList<PostResponse>();
	}

	@Bean
	@Lazy
	public SuccessResponse getSuccessResponseObject() {
		return new SuccessResponse();
	}

	@Bean
	@Lazy
	public ErrorResponse getErrorResponseObject() {
		return new ErrorResponse();
	}

	@Bean
	@Lazy
	public PostResponse getPostResponseObject() {
		return new PostResponse();
	}

	@Bean
	public List<String> getList() {
		return new ArrayList<String>();
	}
	
	@Bean
	@Lazy
	public ObjectMetadata getMetaObject() {
		return new ObjectMetadata();
	}

	@Bean
	@Lazy
	public UserPackages getUSerPackage() {
		return new UserPackages();
	}
	
//	@Bean
//	public JavaMailSender mailSender() {
//		return new JavaMailSenderImpl();
//	}
}
