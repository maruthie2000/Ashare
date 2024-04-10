package com.quantum_paradigm.socialshare.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantum_paradigm.socialshare.configuration.ConfigurationClass;
import com.quantum_paradigm.socialshare.exception.CommonException;
import com.quantum_paradigm.socialshare.response.ResponseStructure;

@Service
public class HashtagService {
	@Autowired
	HttpHeaders headers;

	@Value("${quantum.apiKey}")
	private String ayrshareApiKey;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ConfigurationClass configuration;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ResponseStructure<String> structure;

	public ResponseEntity<ResponseStructure<String>> getAutoHashtags(String post, String position, int max) {
		if (post == "" || post == null) {

			structure.setCode(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Missing or incorrect parameters");
			structure.setStatus("error");
			structure.setHashtag(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.BAD_REQUEST);
		}

		String hashtagUrl = "https://app.ayrshare.com/api/auto-hashtag";
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBearerAuth(ayrshareApiKey);

		String requestBody = "post=" + post + "&position=" + position + "&max=" + max;
		HttpEntity<String> requestEntity = configuration.getHttpEntity(requestBody, headers);
		ResponseEntity<String> response = restTemplate.exchange(hashtagUrl, HttpMethod.POST, requestEntity,
				String.class);
		List<String> hashtags = configuration.getList();
		try {
			JsonNode rootNode = objectMapper.readTree(response.getBody());
			if (rootNode != null) {
				String responseValue = rootNode.has("post") ? rootNode.get("post").asText() : "Invalid keywoed";
				String[] hash = responseValue.split(" ");
				for (String hashtagNode : hash) {
					String hashtag = hashtagNode;
					System.out.println("hashtag " + hashtag);
					hashtags.add(hashtag);
				}
				structure.setCode(HttpStatus.OK.value());
				structure.setHashtag(hashtags);
				structure.setStatus("success");
				structure.setRecommendedHashtag(null);
			}
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new CommonException(e.getLocalizedMessage());
		}
	}

	public ResponseEntity<ResponseStructure<String>> getRecommendedHashtags(String keyword) {
		if (keyword == "" || keyword == null) {
			structure.setCode(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Missing or incorrect keyword");
			structure.setStatus("error");
			structure.setRecommendedHashtag(null);
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.BAD_REQUEST);
		}
		String apiUrl = "https://app.ayrshare.com/api/hashtags/recommend";
		headers.setBearerAuth(ayrshareApiKey);

		HttpEntity<String> requestEntity = configuration.getHttpEntity(headers);
		String requestUrl = apiUrl + "?keyword=" + keyword;
		ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity,
				String.class);
		System.out.println(response);
		Map<String, Long> hashtags = configuration.getMap();
		try {
			JsonNode rootNode = objectMapper.readTree(response.getBody());
			if (rootNode != null) {
				JsonNode recommendationsNode = rootNode.get("recommendations");
				if (recommendationsNode != null && recommendationsNode.isArray()) {
					for (JsonNode recommendation : recommendationsNode) {
						String name = recommendation.has("name") ? recommendation.get("name").asText() : null;
						Long viewCount = recommendation.has("viewCount") ? recommendation.get("viewCount").asLong()
								: null;
						if (name != null && viewCount != null) {
							hashtags.put(name, viewCount);
						}
						structure.setCode(HttpStatus.OK.value());
						structure.setStatus("success");
						structure.setRecommendedHashtag(hashtags);
						structure.setHashtag(null);
					}
					return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);
				} else {
					structure.setCode(HttpStatus.BAD_REQUEST.value());
					structure.setStatus("error");
					structure.setMessage("Not a valid Keyword");
					structure.setHashtag(null);
					structure.setRecommendedHashtag(null);
					return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			return new ResponseEntity<ResponseStructure<String>>(structure, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new CommonException(e.getLocalizedMessage());
		}

	}

}
