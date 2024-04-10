package com.quantum_paradigm.socialshare.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantum_paradigm.socialshare.configuration.ConfigurationClass;
import com.quantum_paradigm.socialshare.dto.MediaPost;
import com.quantum_paradigm.socialshare.exception.CommonException;
import com.quantum_paradigm.socialshare.exception.NullPointerExcep;
import com.quantum_paradigm.socialshare.helper.FormatFile;
import com.quantum_paradigm.socialshare.helper.UploadToServer;
import com.quantum_paradigm.socialshare.response.ErrorResponse;
import com.quantum_paradigm.socialshare.response.PostResponse;
import com.quantum_paradigm.socialshare.response.ResponseStructure;
import com.quantum_paradigm.socialshare.response.ResponseWrapper;
import com.quantum_paradigm.socialshare.response.SuccessResponse;

@Service
public class PostService {

	@Autowired
	UploadToServer uploadToServer;

	@Autowired
	HttpHeaders headers;

	@Autowired
	ConfigurationClass configuration;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	FormatFile formatFile;

	@Autowired
	ResponseStructure<MediaPost> structure;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	PostResponse postResponse;

	@Autowired
	ErrorResponse errorResponse;

	@Autowired
	SuccessResponse successResponse;

	List<PostResponse> responseList = new ArrayList<PostResponse>();

	@Value("${quantum.apiKey}")
	private String bearerToken;

	private ResponseWrapper postImageToProfile(MediaPost mediaPost, MultipartFile mediaFile) {
		System.out.println("Image Method");
		String options = "";
		if (Arrays.toString(mediaPost.getMediaPlatform()).contains("reddit")) {
			if (mediaPost.getTitle() == null)
				mediaPost.setTitle("Shared by Quantum-Share");
			if (mediaPost.getSubreddit() == null)
				mediaPost.setSubreddit("Quantum-Share");
			if (mediaPost.getTitle() != null && mediaPost.getSubreddit() != null) {
				options = "  ,\"redditOptions\": {\n" + "        \"title\": \"" + mediaPost.getTitle() + "\",\n"
						+ "      \"subreddit\": \"" + mediaPost.getSubreddit() + "\"\n" + "    }\n";
			} else {
				errorResponse.setMessage(
						"Title and Subreddit Required. Please provide a title and specify a subreddit for the Reddit platform.");
				errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
				errorResponse.setStatus("error");
				postResponse.addErrorResponse(errorResponse);
				responseList.add(postResponse);
				return new ResponseWrapper(responseList);
			}
		}
		try {
			String fileurl = uploadToServer.uploadFile(mediaFile);
			System.out.println("cap " + mediaPost.getCaption());
			String postUrl = "https://app.ayrshare.com/api/post";
			String jsonString = "{\n" + "    \"post\": \"" + mediaPost.getCaption() + " \",\n" + "    \"platforms\": "
					+ getMedia(mediaPost.getMediaPlatform()) + ",\n" + "    \"mediaUrls\": \"" + fileurl + "\"\n"
					+ options + "}";
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(bearerToken);
			System.out.println("HTTP Heders");
			HttpEntity<String> requestEntity = configuration.getHttpEntity(jsonString, headers);

			ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.POST, requestEntity,
					String.class);
			System.out.println(response.toString());
			if (response.getStatusCode().is2xxSuccessful()) {
				responseList.clear();
				PostResponse postResponse = configuration.getPostResponseObject();
				SuccessResponse successResponse = configuration.getSuccessResponseObject();
				successResponse.setStatus("success");
				successResponse.setMessage("media posted successfully");
				successResponse.setPlatforms(Arrays.toString(mediaPost.getMediaPlatform()));
				successResponse.setCode(HttpStatus.OK.value());
				postResponse.addSuccessResponse(successResponse);
				responseList.add(postResponse);
				return new ResponseWrapper(responseList);
			}
			JsonNode responseJson = objectMapper.readTree(response.getBody());
			if ((response.getStatusCode().isError())) {
				return handleHttpClientAndServer(responseJson.toString());
			} else {
				errorResponse.setCode(response.getStatusCode().value());
				errorResponse.setStatus("error");
				errorResponse.setMessage(response.getBody());
				postResponse.addErrorResponse(errorResponse);
				responseList.add(postResponse);
				return new ResponseWrapper(responseList);
			}
		}
//		catch (InternalServerException e) {
//			throw new InternalServerException(e.getMessage());
//		}
		catch (JsonMappingException e) {
			throw new CommonException(e.getMessage());
		} catch (JsonProcessingException e) {
			throw new CommonException(e.getMessage());
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			try {
				JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());

				if (rootNode.has("errors") || rootNode.has("postIds")) {
					return handleHttpClientAndServer(e.getResponseBodyAsString());
				} else {
					try {
						String response = e.getResponseBodyAsString();
						JsonNode responseJson = objectMapper.readTree(response);
						structure.setCode(responseJson.has("code") ? responseJson.get("code").asInt() : -1);
						structure.setMessage(responseJson.has("message")
								? responseJson.get("message").asText().split("\\.")[0].trim()
								: "Unknown error");
						structure.setStatus("error");

						return new ResponseWrapper(structure);
					} catch (JsonMappingException e1) {
						throw new CommonException(e.getMessage());

					} catch (JsonProcessingException e1) {
						throw new CommonException(e.getMessage());
					}
				}
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
				return null;
			}
		} catch (NullPointerException e) {
			throw new NullPointerExcep(e.getMessage());
		}
	}

	private ResponseWrapper handleHttpClientAndServer(String responseBodyAsString) {
		List<PostResponse> responseList = new ArrayList<>();
		try {
//			JsonNode responseJson = objectMapper.readTree(responseBodyAsString);
			JsonNode rootNode = objectMapper.readTree(responseBodyAsString);
			if (rootNode.has("errors") || rootNode.has("postIds")) {

			} else {
				// Call another method or handle the case where neither "errors" nor "postIds"
				// are present
			}
			JsonNode errorsNode = rootNode.get("errors");
			JsonNode postIdsNode = rootNode.get("postIds");

			// Handling error responses
			if (errorsNode != null && errorsNode.isArray()) {
				List<ErrorResponse> errorResponses = new ArrayList<>();
				for (JsonNode errorNode : errorsNode) {
					ErrorResponse errorResponse = configuration.getErrorResponseObject();
					errorResponse.setStatus("error");
					errorResponse.setCode(errorNode.has("code") ? errorNode.get("code").asInt() : -1);
					errorResponse.setMessage(
							errorNode.has("message") ? errorNode.get("message").asText().split("https")[0].trim()
									: "Unknown error");
					System.out.println(errorNode.get("message").asText());
					errorResponse.setPlatforms(
							errorNode.has("platform") ? errorNode.get("platform").asText() : "Unknown platform");
					errorResponses.add(errorResponse);
				}
				PostResponse errorPostResponse = configuration.getPostResponseObject();
				errorPostResponse.setErrorResponses(errorResponses);
				responseList.add(errorPostResponse);
			}

			// Handling success responses
			if (postIdsNode != null && postIdsNode.isArray()) {
				List<SuccessResponse> successResponses = new ArrayList<>();
				for (JsonNode postIdNode : postIdsNode) {
					SuccessResponse successResponse = configuration.getSuccessResponseObject();
					successResponse.setStatus("success");
					successResponse.setCode(HttpStatus.OK.value()); // Assuming 200 OK for success
					successResponse.setMessage("Post successful");
					successResponse.setPlatforms(
							postIdNode.has("platform") ? postIdNode.get("platform").asText() : "Unknown platform");
					successResponses.add(successResponse);
				}
				PostResponse successPostResponse = configuration.getPostResponseObject();
				successPostResponse.setSuccessResponses(successResponses);
				responseList.add(successPostResponse);
			}

			return new ResponseWrapper(responseList);
		} catch (IOException e) {
			throw new CommonException(e.getMessage());
		}
	}

	public String getMedia(String[] platforms) {
		String output = "[";
		for (int i = 0; i < platforms.length; i++) {
			if (i > 0) {
				output += ", ";
			}
			output += "\"" + platforms[i] + "\"";
		}
		output += "]";
		return output;
	}

	public ResponseWrapper postMedia(MediaPost mediaPost, MultipartFile mediaFile) {
		System.out.println("Post Media Method");
		if (mediaPost.getCaption() == null||mediaPost.getCaption() == "") {
			System.out.println("Empty Captions");
			mediaPost.setCaption("_");
		}

		String contentType = mediaFile.getContentType();
		List<PostResponse> responseList = null;
		if (contentType.startsWith("image")) {
			if (contentType.equals("image/jpg") || contentType.equals("image/jpeg") || contentType.equals("image/png")
					|| contentType.equals("image/webp")) {
				System.out.println("calling image method");
				return postImageToProfile(mediaPost, mediaFile);

			} else {
				structure.setStatus("error");
				structure.setMessage(
						"Unsupported Image Format. Please ensure the file type is one of the following: jpg, jpeg, png, or webp");

				structure.setCode(HttpStatus.BAD_REQUEST.value());
				return new ResponseWrapper(structure);
			}
		} else if (contentType.startsWith("video")) {
			if (contentType.equals("video/mp4")) {
				System.out.println("calling image method");
				return postVideoToProfile(mediaPost, mediaFile);
			} else {
				System.out.println("Error in content typre");
				structure.setMessage("Unsupported Video Format. Please ensure the file type mp4/gif");
				structure.setStatus("error");
				structure.setCode(HttpStatus.BAD_REQUEST.value());
				return new ResponseWrapper(structure);
			}
		} else {
			structure.setMessage("Unsupported Media File Format.");
			structure.setStatus("error");
			structure.setCode(HttpStatus.BAD_REQUEST.value());
			return new ResponseWrapper(responseList, structure);
		}
	}

	private ResponseWrapper postVideoToProfile(MediaPost mediaPost, MultipartFile mediaFile) {
		if(mediaPost.getTumbNail()==null||mediaPost.getTumbNail()=="")
			mediaPost.setTumbNail("https://planotech123.s3.ap-south-1.amazonaws.com/permanent/thumbnail.jpeg");
		String options = "";
		if (Arrays.toString(mediaPost.getMediaPlatform()).contains("youtube")) {
			if (mediaPost.getTitle() == null) {
				mediaPost.setTitle("Quantum Share");
			}
			options = options + "   , \"youTubeOptions\": {\n" + "        \"title\": \"" + mediaPost.getTitle()
					+ "\",\n" + "\"visibility\": \"public\"\n" + "    }\n";
		}
		if (Arrays.toString(mediaPost.getMediaPlatform()).contains("pinterest")) {
			if(mediaPost.getTumbNail()==null||mediaPost.getTumbNail()=="")
				mediaPost.setTumbNail("https://planotech123.s3.ap-south-1.amazonaws.com/permanent/thumbnail.jpeg");

			if (mediaPost.getTumbNail() == null)
				mediaPost.setTumbNail(""); // SET TUMBNAIL
			options = options + ",\"pinterestOptions\": {\n" + "        \"thumbNail\": \"" + mediaPost.getTumbNail()
					+ "\"\n" + "    }\n";
		}
		if (Arrays.toString(mediaPost.getMediaPlatform()).contains("reddit")) {
			if (mediaPost.getTitle() == null) {
				mediaPost.setTitle("Quantum Share");
			}
			if (mediaPost.getSubreddit() == null)
				mediaPost.setSubreddit("please review the subreddit guidelines.");
			options = options + "    ,\"redditOptions\": {\n" + "        \"title\": \"" + mediaPost.getTitle() + "\",\n"
					+ "        \"subreddit\": \"" + mediaPost.getSubreddit() + "\"\n" + "    }\n";
		}
		try {
			String fileurl = uploadToServer.uploadFile(mediaFile);
			String postUrl = "https://api.ayrshare.com/api/post";
			String jsonString = "{\n" + "    \"post\": \"" + mediaPost.getCaption() + "\",\n" + "    \"platforms\": "
					+ getMedia(mediaPost.getMediaPlatform()) + ",\n" + "    \"mediaUrls\": \"" + fileurl + "\",\n"
					+ "    \"isVideo\": \"true\"\n" + options + "}";

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(bearerToken);
			HttpEntity<String> requestEntity = configuration.getHttpEntity(jsonString, headers);
			ResponseEntity<String> response = restTemplate.exchange(postUrl, HttpMethod.POST, requestEntity,
					String.class);
			System.out.println(response.toString());
			if (response.getStatusCode().is2xxSuccessful()) {
				responseList.clear();
				PostResponse postResponse = configuration.getPostResponseObject();
				SuccessResponse successResponse = configuration.getSuccessResponseObject();
				successResponse.setStatus("success");
				successResponse.setMessage("media posted successfully");
				successResponse.setPlatforms(Arrays.toString(mediaPost.getMediaPlatform()));
				successResponse.setCode(HttpStatus.OK.value());
				postResponse.addSuccessResponse(successResponse);
				responseList.add(postResponse);
				return new ResponseWrapper(responseList);
			}
			JsonNode responseJson = objectMapper.readTree(response.getBody());
			if ((response.getStatusCode().isError())) {
				return handleHttpClientAndServer(responseJson.toString());
			} else {
				errorResponse.setCode(response.getStatusCode().value());
				errorResponse.setStatus("error");
				errorResponse.setMessage(response.getBody());
				postResponse.addErrorResponse(errorResponse);
				responseList.add(postResponse);
				return new ResponseWrapper(responseList);
			}
		} catch (JsonMappingException e) {
			throw new CommonException(e.getMessage());
		} catch (JsonProcessingException e) {
			throw new CommonException(e.getMessage());
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			try {
				JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());

				if (rootNode.has("errors") || rootNode.has("postIds")) {
					return handleHttpClientAndServer(e.getResponseBodyAsString());
				} else {
					try {
						String response = e.getResponseBodyAsString();
						JsonNode responseJson = objectMapper.readTree(response);
						structure.setCode(responseJson.has("code") ? responseJson.get("code").asInt() : -1);
						structure.setMessage(responseJson.has("message")
								? responseJson.get("message").asText().split("\\.")[0].trim()
								: "Unknown error");
						structure.setStatus("error");

						return new ResponseWrapper(structure);
					} catch (JsonMappingException e1) {
						throw new CommonException(e.getMessage());
					} catch (JsonProcessingException e1) {
						throw new CommonException(e.getMessage());
					}
				}
			} catch (JsonProcessingException e1) {
				throw new CommonException(e.getMessage());
			}
		} catch (NullPointerException e) {
			throw new NullPointerExcep(e.getMessage());
		}
	}

}
