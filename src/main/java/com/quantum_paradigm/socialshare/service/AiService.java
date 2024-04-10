package com.quantum_paradigm.socialshare.service;

import java.util.Collections;

import org.apache.commons.codec.binary.Base64;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantum_paradigm.socialshare.response.ResponseStructure;



@Service
public class AiService {

	 @Value("${openai.api.key}")
	 private String openAIKey;
	 
	 @Value("${stability.api.url}")
	 private String apiUrl;

	 @Value("${stability.api.token}")
	 private String apiToken;
	 
	 @Autowired
	 ResponseStructure<String> responseStructure;
	 
	 @Autowired
	 ResponseStructure<byte[]> responsedStructure;
	 
	 @Autowired
	 RestTemplate restTemplate;
	 
	 @Autowired
	 HttpHeaders headers;
	 
	 @Autowired
	 HttpEntity<String> httpEntity;
	 
	 @Autowired
	 ObjectMapper objectMapper;
	 
	 public ResponseEntity<ResponseStructure<String>> aiChat(String userMessage, String systemMessage) {
	        String url = "https://api.openai.com/v1/chat/completions";

	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setBearerAuth(openAIKey); // Assuming openAIKey is defined somewhere

	        String requestBody = "{\n" +
	                "    \"model\": \"gpt-3.5-turbo\",\n" +
	                "    \"messages\": [\n" +
	                "        {\n" +
	                "            \"role\": \"system\",\n" +
	                "            \"content\": \"" + systemMessage + "\"\n" +
	                "        },\n" +
	                "        {\n" +
	                "            \"role\": \"user\",\n" +
	                "            \"content\": \"" + userMessage + "\"\n" +
	                "        }\n" +
	                "    ]\n" +
	                "}";

	        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

	        try {
	            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
	            int statusCode = responseEntity.getStatusCode().value();
	            String responseBody = responseEntity.getBody();
	            
	            responseStructure.setStatus("Success");
	            responseStructure.setMessage("OK");
	            responseStructure.setCode(statusCode);

	            // Parse the response body to extract the "content" field
	            JsonNode jsonNode = objectMapper.readTree(responseBody);
	            String content = jsonNode.path("choices").get(0).path("message").path("content").asText();
	            responseStructure.setData(content);

	            return ResponseEntity.status(statusCode).body(responseStructure);
	        } catch (Exception e) {
	            responseStructure.setStatus("Error");
	            responseStructure.setMessage("Failed");
	            responseStructure.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseStructure);
	        }
	    }
	 
	 public byte[] generateImage(String textPrompt) {
		 
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    headers.setAccept(Collections.singletonList(MediaType.IMAGE_PNG));
		    headers.setBearerAuth(apiToken);

		    // Define request body
		    String requestBody = "{\"text_prompts\": [{\"text\": \"" + textPrompt + "\"}],\"cfg_scale\": 7,\"height\": 320,\"width\": 320,\"samples\": 1,\"steps\": 30}";
		    HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

		    try {
		        // Make the HTTP request
		        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, byte[].class);

		        // Check response status
		        HttpStatusCode statusCode = responseEntity.getStatusCode(); 
		        if (statusCode == HttpStatus.OK) {
		            return responseEntity.getBody();
		        }else {
		            throw new RuntimeException("Failed to generate image. Status code: " + statusCode.value());
		        }
		    } catch (HttpClientErrorException e) {
		        // Handle client-side errors (4xx)
		        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
		            throw new RuntimeException("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Unauthorized access/Token.\"}");
		        } else {
		            throw new RuntimeException("{\"status\":" + e.getStatusCode().value() + ",\"error\":\"Client Error\",\"message\":\"" + e.getStatusText() + "\"}");
		        }
		    } catch (HttpServerErrorException e) {
		        // Handle server-side errors (5xx)
		        throw new RuntimeException("{\"status\":" + e.getStatusCode().value() + ",\"error\":\"Server Error\",\"message\":\"" + e.getStatusText() + "\"}");
		    } catch (RestClientException e) {
		        // Handle other RestClientExceptions
		        throw new RuntimeException("{\"status\":500,\"error\":\"Rest Client Error\",\"message\":\"" + e.getMessage() + "\"}");
		    }
	 }
}
