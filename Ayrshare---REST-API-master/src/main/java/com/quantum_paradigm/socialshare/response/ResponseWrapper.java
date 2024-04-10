package com.quantum_paradigm.socialshare.response;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quantum_paradigm.socialshare.dto.MediaPost;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ResponseWrapper {

	List<PostResponse> postResponse;
	ResponseStructure<MediaPost> responseStructure;
	HttpStatus httpStatus;

	public ResponseWrapper(List<PostResponse> postResponse, ResponseStructure<MediaPost> responseStructure,
			HttpStatus httpStatus) {
		this.postResponse = postResponse;
		this.responseStructure = responseStructure;
		this.httpStatus = httpStatus;
	}

	public ResponseWrapper(ResponseStructure<MediaPost> responseStructure, HttpStatus httpStatus) {
		this.responseStructure = responseStructure;
		this.httpStatus = httpStatus;
	}

	public ResponseWrapper(List<PostResponse> postResponse) {
		this.postResponse = postResponse;

	}

	public ResponseWrapper(ResponseStructure<MediaPost> responseStructure) {
		this.responseStructure = responseStructure;
	}

	public ResponseWrapper(List<PostResponse> responseList, ResponseStructure<MediaPost> responseStructure) {
		this.responseStructure = responseStructure;
		this.postResponse = responseList;
	}

	@Override
	public String toString() {
		return "ResponseWrapper{" + "errorResponse=" + postResponse + ", responseStructure=" + responseStructure + '}';
	}

}
