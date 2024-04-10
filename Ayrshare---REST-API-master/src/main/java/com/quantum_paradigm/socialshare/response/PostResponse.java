package com.quantum_paradigm.socialshare.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {

	List<SuccessResponse> successResponses;
	List<ErrorResponse> errorResponses;
	
	public void addErrorResponse(ErrorResponse errorResponse) {
        if (errorResponses == null) {
            errorResponses = new ArrayList<>();
        }
        errorResponses.add(errorResponse);
    }
	public void addSuccessResponse(SuccessResponse successResponse) {
        if (successResponses == null) {
            successResponses = new ArrayList<>();
        }
        successResponses.add(successResponse);
    }
}
