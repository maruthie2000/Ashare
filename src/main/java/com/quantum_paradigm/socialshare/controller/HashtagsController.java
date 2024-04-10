package com.quantum_paradigm.socialshare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quantum_paradigm.socialshare.response.ResponseStructure;
import com.quantum_paradigm.socialshare.service.HashtagService;

@RestController
@RequestMapping("/quantum-socialshare")
public class HashtagsController {

	@Autowired
	HashtagService hashtagService;

	@PostMapping("/generate")
	public ResponseEntity<ResponseStructure<String>> generateAutoHashtags(@RequestParam String keyword) {
		System.out.println("controller "+keyword);
		String position = "end";
		int max = 10;
		return hashtagService.getAutoHashtags(keyword, position, max);

	}
	
	 @GetMapping("/recommend")
	    public ResponseEntity<ResponseStructure<String>> getRecommendedHashtags(@RequestParam String keyword) {
	        return hashtagService.getRecommendedHashtags(keyword);
	    }
}
